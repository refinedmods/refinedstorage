package refinedstorage.tile;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import refinedstorage.*;
import refinedstorage.block.BlockController;
import refinedstorage.block.EnumControllerType;
import refinedstorage.container.ContainerController;
import refinedstorage.item.ItemPattern;
import refinedstorage.item.ItemWirelessGrid;
import refinedstorage.network.GridPullFlags;
import refinedstorage.network.MessageWirelessGridItems;
import refinedstorage.storage.IStorage;
import refinedstorage.storage.IStorageProvider;
import refinedstorage.storage.ItemGroup;
import refinedstorage.tile.autocrafting.CraftingPattern;
import refinedstorage.tile.autocrafting.TileCrafter;
import refinedstorage.tile.autocrafting.task.BasicCraftingTask;
import refinedstorage.tile.autocrafting.task.ICraftingTask;
import refinedstorage.tile.autocrafting.task.ProcessingCraftingTask;
import refinedstorage.tile.config.IRedstoneModeConfig;
import refinedstorage.tile.config.RedstoneMode;
import refinedstorage.tile.grid.WirelessGridConsumer;

import java.util.*;

public class TileController extends TileBase implements IEnergyReceiver, ISynchronizedContainer, IRedstoneModeConfig {
    public class ClientSideMachine {
        public ItemStack stack;
        public int amount;
        public int energyUsage;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ClientSideMachine other = (ClientSideMachine) o;

            return energyUsage == other.energyUsage && RefinedStorageUtils.compareStack(stack, other.stack);
        }

        @Override
        public int hashCode() {
            int result = stack.hashCode();
            result = 31 * result + energyUsage;
            return result;
        }
    }

    public static final int ENERGY_CAPACITY = 32000;

    public static final String NBT_CRAFTING_TASKS = "CraftingTasks";
    public static final String NBT_DESC_ENERGY = "Energy";

    public static final int MAX_CRAFTING_QUANTITY_PER_REQUEST = 100;

    private List<ItemGroup> itemGroups = new ArrayList<ItemGroup>();
    private List<IStorage> storages = new ArrayList<IStorage>();
    private List<WirelessGridConsumer> wirelessGridConsumers = new ArrayList<WirelessGridConsumer>();
    private List<WirelessGridConsumer> wirelessGridConsumersToRemove = new ArrayList<WirelessGridConsumer>();

    private Set<Integer> combinedGroupsIndices = new HashSet<Integer>();
    private List<ItemGroup> combinedGroups = new ArrayList<ItemGroup>();

    private RedstoneMode redstoneMode = RedstoneMode.IGNORE;

    private List<TileMachine> machines = new ArrayList<TileMachine>();
    private List<TileMachine> machinesToAdd = new ArrayList<TileMachine>();
    private List<TileMachine> machinesToRemove = new ArrayList<TileMachine>();

    private List<ClientSideMachine> clientSideMachines = new ArrayList<ClientSideMachine>();

    private List<CraftingPattern> patterns = new ArrayList<CraftingPattern>();
    private List<ICraftingTask> craftingTasks = new ArrayList<ICraftingTask>();
    private List<ICraftingTask> craftingTasksToAdd = new ArrayList<ICraftingTask>();
    private List<ICraftingTask> craftingTasksToCancel = new ArrayList<ICraftingTask>();

    private EnergyStorage energy = new EnergyStorage(ENERGY_CAPACITY);
    private int energyUsage;

    private boolean couldRun;

    private long lastEnergyUpdate;

    private int wirelessGridRange;

    public void addMachine(TileMachine machine) {
        machinesToAdd.add(machine);
    }

    public void removeMachine(TileMachine machine) {
        machinesToRemove.add(machine);
    }

    @Override
    public void update() {
        super.update();

        if (!worldObj.isRemote) {
            // Prevent cache from re-adding the block
            for (TileMachine machine : machinesToAdd) {
                if (!machines.contains(machine)) {
                    machines.add(machine);
                }
            }
            machinesToAdd.clear();

            machines.removeAll(machinesToRemove);
            machinesToRemove.clear();

            int lastEnergy = energy.getEnergyStored();

            if (mayRun()) {
                if (ticks % 20 == 0) {
                    syncMachines();
                }

                for (TileMachine machine : machines) {
                    if (machine.mayUpdate()) {
                        machine.updateMachine();
                    }
                }

                for (ICraftingTask taskToCancel : craftingTasksToCancel) {
                    taskToCancel.onCancelled(this);
                }
                craftingTasks.removeAll(craftingTasksToCancel);
                craftingTasksToCancel.clear();

                craftingTasks.addAll(craftingTasksToAdd);
                craftingTasksToAdd.clear();

                Iterator<ICraftingTask> craftingTaskIterator = craftingTasks.iterator();

                while (craftingTaskIterator.hasNext()) {
                    ICraftingTask task = craftingTaskIterator.next();

                    if (ticks % task.getPattern().getCrafter(worldObj).getSpeed() == 0 && task.update(this)) {
                        task.onDone(this);

                        craftingTaskIterator.remove();
                    }
                }
            } else {
                disconnectAll();
            }

            if (couldRun != mayRun()) {
                couldRun = mayRun();

                worldObj.notifyNeighborsOfStateChange(pos, RefinedStorageBlocks.CONTROLLER);
            }

            wirelessGridConsumers.removeAll(wirelessGridConsumersToRemove);
            wirelessGridConsumersToRemove.clear();

            Iterator<WirelessGridConsumer> gridConsumerIterator = wirelessGridConsumers.iterator();

            while (gridConsumerIterator.hasNext()) {
                WirelessGridConsumer consumer = gridConsumerIterator.next();

                if (!RefinedStorageUtils.compareStack(consumer.getWirelessGrid(), consumer.getPlayer().getHeldItem(consumer.getHand()))) {
                    consumer.getPlayer().closeScreen(); // This will call onContainerClosed on the Container and remove it from the list
                } else {
                    if (mayRun()) {
                        RefinedStorage.NETWORK.sendTo(new MessageWirelessGridItems(this), (EntityPlayerMP) consumer.getPlayer());
                    }
                }
            }

            if (getType() == EnumControllerType.NORMAL && energyUsage > 0) {
                if (energy.getEnergyStored() - energyUsage >= 0) {
                    energy.extractEnergy(energyUsage, false);
                } else {
                    energy.setEnergyStored(0);
                }
            } else if (getType() == EnumControllerType.CREATIVE) {
                energy.setEnergyStored(energy.getMaxEnergyStored());
            }

            if (energy.getEnergyStored() != lastEnergy) {
                worldObj.updateComparatorOutputLevel(pos, RefinedStorageBlocks.CONTROLLER);

                if (System.currentTimeMillis() - lastEnergyUpdate > 5000) {
                    lastEnergyUpdate = System.currentTimeMillis();

                    RefinedStorageUtils.updateBlock(worldObj, pos);
                }
            }
        }
    }

    public void syncMachines() {
        this.wirelessGridRange = 0;
        this.energyUsage = 0;
        this.storages.clear();
        this.patterns.clear();

        for (TileMachine machine : machines) {
            if (!machine.mayUpdate()) {
                continue;
            }

            if (machine instanceof TileWirelessTransmitter) {
                this.wirelessGridRange += ((TileWirelessTransmitter) machine).getRange();
            }

            if (machine instanceof IStorageProvider) {
                ((IStorageProvider) machine).provide(storages);
            }

            if (machine instanceof TileCrafter) {
                TileCrafter crafter = (TileCrafter) machine;

                for (int i = 0; i < TileCrafter.PATTERN_SLOTS; ++i) {
                    if (crafter.getStackInSlot(i) != null) {
                        ItemStack pattern = crafter.getStackInSlot(i);

                        patterns.add(new CraftingPattern(crafter.getPos().getX(), crafter.getPos().getY(), crafter.getPos().getZ(), ItemPattern.isProcessing(pattern), ItemPattern.getInputs(pattern), ItemPattern.getOutputs(pattern)));
                    }
                }
            }

            this.energyUsage += machine.getEnergyUsage();
        }

        Collections.sort(storages, new Comparator<IStorage>() {
            @Override
            public int compare(IStorage left, IStorage right) {
                if (left.getPriority() == right.getPriority()) {
                    return 0;
                }

                return (left.getPriority() > right.getPriority()) ? -1 : 1;
            }
        });

        syncItems();
    }

    public EnumControllerType getType() {
        if (worldObj.getBlockState(pos).getBlock() == RefinedStorageBlocks.CONTROLLER) {
            return (EnumControllerType) worldObj.getBlockState(pos).getValue(BlockController.TYPE);
        }

        return EnumControllerType.NORMAL;
    }

    public int getWirelessGridRange() {
        return wirelessGridRange;
    }

    private void disconnectAll() {
        for (TileMachine machine : machines) {
            machine.onDisconnected(worldObj);
        }

        machines.clear();
    }

    public List<ItemGroup> getItemGroups() {
        return itemGroups;
    }

    public List<ICraftingTask> getCraftingTasks() {
        return craftingTasks;
    }

    public void addCraftingTask(ICraftingTask task) {
        craftingTasksToAdd.add(task);
    }

    public void addCraftingTask(CraftingPattern pattern) {
        if (pattern.isProcessing()) {
            addCraftingTask(new ProcessingCraftingTask(pattern));
        } else {
            addCraftingTask(new BasicCraftingTask(pattern));
        }
    }

    public void cancelCraftingTask(ICraftingTask task) {
        craftingTasksToCancel.add(task);
    }

    public List<CraftingPattern> getPatterns() {
        return patterns;
    }

    public CraftingPattern getPattern(ItemStack pattern) {
        return getPattern(pattern, RefinedStorageUtils.COMPARE_DAMAGE | RefinedStorageUtils.COMPARE_NBT);
    }

    public CraftingPattern getPattern(ItemStack pattern, int flags) {
        for (CraftingPattern craftingPattern : getPatterns()) {
            for (ItemStack output : craftingPattern.getOutputs()) {
                if (RefinedStorageUtils.compareStack(output, pattern, flags)) {
                    return craftingPattern;
                }
            }
        }

        return null;
    }

    private void syncItems() {
        itemGroups.clear();

        for (IStorage storage : storages) {
            storage.addItems(itemGroups);
        }

        for (CraftingPattern pattern : patterns) {
            for (ItemStack output : pattern.getOutputs()) {
                ItemGroup patternGroup = new ItemGroup(output);
                patternGroup.setQuantity(0);
                itemGroups.add(patternGroup);
            }
        }

        combinedGroups.clear();
        combinedGroupsIndices.clear();

        for (int i = 0; i < itemGroups.size(); ++i) {
            if (combinedGroupsIndices.contains(i)) {
                continue;
            }

            ItemGroup group = itemGroups.get(i);

            // If the item doesn't exist anymore, remove it from storage to avoid crashes
            if (group.getType() == null) {
                combinedGroups.add(group);
                combinedGroupsIndices.add(i);
            } else {
                for (int j = i + 1; j < itemGroups.size(); ++j) {
                    if (combinedGroupsIndices.contains(j)) {
                        continue;
                    }

                    ItemGroup otherGroup = itemGroups.get(j);

                    if (group.compareNoQuantity(otherGroup)) {
                        // We copy here so we don't modify the quantity of the item group IStorage uses
                        itemGroups.set(i, group.copy(group.getQuantity() + otherGroup.getQuantity()));

                        combinedGroups.add(otherGroup);
                        combinedGroupsIndices.add(j);
                    }
                }
            }
        }

        itemGroups.removeAll(combinedGroups);
    }

    public boolean push(ItemStack stack) {
        for (IStorage storage : storages) {
            if (storage.mayPush(stack)) {
                storage.push(stack);

                syncItems();

                // Notify processing tasks that we got an item
                // A processing task accepts itemstacks of 1 item, so give it like that
                for (int i = 0; i < stack.stackSize; ++i) {
                    for (ICraftingTask task : craftingTasks) {
                        if (task instanceof ProcessingCraftingTask) {
                            if (((ProcessingCraftingTask) task).onInserted(stack)) {
                                break;
                            }
                        }
                    }
                }

                return true;
            }
        }

        return false;
    }

    public ItemStack take(ItemStack stack) {
        return take(stack, RefinedStorageUtils.COMPARE_DAMAGE | RefinedStorageUtils.COMPARE_NBT);
    }

    public ItemStack take(ItemStack stack, int flags) {
        int requested = stack.stackSize;
        int receiving = 0;

        ItemStack newStack = null;

        for (IStorage storage : storages) {
            ItemStack took = storage.take(stack, flags);

            if (took != null) {
                if (newStack == null) {
                    newStack = took;
                } else {
                    newStack.stackSize += took.stackSize;
                }

                receiving += took.stackSize;
            }

            if (requested == receiving) {
                break;
            }
        }

        if (newStack != null) {
            syncItems();
        }

        return newStack;
    }

    public void setEnergyStored(int amount) {
        energy.setEnergyStored(amount);
    }

    public boolean onOpenWirelessGrid(EntityPlayer player, EnumHand hand) {
        boolean inRange = (int) Math.sqrt(Math.pow(getPos().getX() - player.posX, 2) + Math.pow(getPos().getY() - player.posY, 2) + Math.pow(getPos().getZ() - player.posZ, 2)) < getWirelessGridRange();

        if (!inRange) {
            return false;
        }

        wirelessGridConsumers.add(new WirelessGridConsumer(player, hand, player.getHeldItem(hand)));

        player.openGui(RefinedStorage.INSTANCE, RefinedStorageGui.WIRELESS_GRID, worldObj, RefinedStorageUtils.getIdFromHand(hand), 0, 0);

        drainEnergyFromWirelessGrid(player, ItemWirelessGrid.USAGE_OPEN);

        return true;
    }

    public void onCloseWirelessGrid(EntityPlayer player) {
        WirelessGridConsumer consumer = getWirelessGridConsumer(player);

        if (consumer != null) {
            wirelessGridConsumersToRemove.add(consumer);
        }
    }

    public void drainEnergyFromWirelessGrid(EntityPlayer player, int energy) {
        WirelessGridConsumer consumer = getWirelessGridConsumer(player);

        if (consumer != null) {
            ItemWirelessGrid item = RefinedStorageItems.WIRELESS_GRID;
            ItemStack held = consumer.getPlayer().getHeldItem(consumer.getHand());

            if (held.getItemDamage() != ItemWirelessGrid.TYPE_CREATIVE) {
                item.extractEnergy(held, energy, false);

                if (item.getEnergyStored(held) <= 0) {
                    onCloseWirelessGrid(player);
                    consumer.getPlayer().closeScreen();
                }
            }
        }
    }

    public WirelessGridConsumer getWirelessGridConsumer(EntityPlayer player) {
        Iterator<WirelessGridConsumer> it = wirelessGridConsumers.iterator();

        while (it.hasNext()) {
            WirelessGridConsumer consumer = it.next();

            if (consumer.getPlayer() == player) {
                return consumer;
            }
        }

        return null;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        energy.readFromNBT(nbt);

        if (nbt.hasKey(RedstoneMode.NBT)) {
            redstoneMode = RedstoneMode.getById(nbt.getInteger(RedstoneMode.NBT));
        }

        if (nbt.hasKey(NBT_CRAFTING_TASKS)) {
            NBTTagList taskList = nbt.getTagList(NBT_CRAFTING_TASKS, Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < taskList.tagCount(); ++i) {
                NBTTagCompound taskTag = taskList.getCompoundTagAt(i);

                switch (taskTag.getInteger("Type")) {
                    case BasicCraftingTask.ID:
                        addCraftingTask(new BasicCraftingTask(taskTag));
                        break;
                    case ProcessingCraftingTask.ID:
                        addCraftingTask(new ProcessingCraftingTask(taskTag));
                        break;
                }
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        energy.writeToNBT(nbt);

        nbt.setInteger(RedstoneMode.NBT, redstoneMode.id);

        NBTTagList list = new NBTTagList();

        for (ICraftingTask task : craftingTasks) {
            NBTTagCompound taskTag = new NBTTagCompound();
            task.writeToNBT(taskTag);
            list.appendTag(taskTag);
        }

        nbt.setTag(NBT_CRAFTING_TASKS, list);
    }

    @Override
    public void writeToDescriptionPacketNBT(NBTTagCompound tag) {
        super.writeToDescriptionPacketNBT(tag);

        tag.setInteger(NBT_DESC_ENERGY, getEnergyStored(null));
    }

    @Override
    public void readFromDescriptionPacketNBT(NBTTagCompound tag) {
        super.readFromDescriptionPacketNBT(tag);

        setEnergyStored(tag.getInteger(NBT_DESC_ENERGY));
    }

    @Override
    public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
        return energy.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int getEnergyStored(EnumFacing from) {
        return energy.getEnergyStored();
    }

    public int getEnergyScaled(int i) {
        return (int) ((float) energy.getEnergyStored() / (float) energy.getMaxEnergyStored() * (float) i);
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return energy.getMaxEnergyStored();
    }

    public int getEnergyUsage() {
        return energyUsage;
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return true;
    }

    public boolean mayRun() {
        return energy.getEnergyStored() > 0 && energy.getEnergyStored() >= energyUsage && redstoneMode.isEnabled(worldObj, pos);
    }

    @Override
    public RedstoneMode getRedstoneMode() {
        return redstoneMode;
    }

    @Override
    public void setRedstoneMode(RedstoneMode mode) {
        markDirty();

        this.redstoneMode = mode;
    }

    @Override
    public BlockPos getMachinePos() {
        return pos;
    }

    public List<ClientSideMachine> getClientSideMachines() {
        return clientSideMachines;
    }

    @Override
    public void receiveContainerData(ByteBuf buf) {
        setEnergyStored(buf.readInt());
        energyUsage = buf.readInt();

        redstoneMode = RedstoneMode.getById(buf.readInt());

        machines.clear();

        List<ClientSideMachine> machines = new ArrayList<ClientSideMachine>();

        int size = buf.readInt();

        for (int i = 0; i < size; ++i) {
            ClientSideMachine machine = new ClientSideMachine();
            machine.energyUsage = buf.readInt();
            machine.amount = buf.readInt();
            machine.stack = ByteBufUtils.readItemStack(buf);

            machines.add(machine);
        }

        clientSideMachines = machines;
    }

    @Override
    public void sendContainerData(ByteBuf buf) {
        buf.writeInt(getEnergyStored(null));
        buf.writeInt(energyUsage);

        buf.writeInt(redstoneMode.id);

        List<ClientSideMachine> m = new ArrayList<ClientSideMachine>();

        for (TileMachine machine : machines) {
            if (machine.mayUpdate()) {
                IBlockState state = worldObj.getBlockState(machine.getPos());

                ClientSideMachine clientMachine = new ClientSideMachine();

                clientMachine.energyUsage = machine.getEnergyUsage();
                clientMachine.amount = 1;
                clientMachine.stack = new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));

                if (m.contains(clientMachine)) {
                    for (ClientSideMachine other : m) {
                        if (other.equals(clientMachine)) {
                            other.amount++;
                            break;
                        }
                    }
                } else {
                    m.add(clientMachine);
                }
            }
        }

        buf.writeInt(m.size());

        for (ClientSideMachine machine : m) {
            buf.writeInt(machine.energyUsage);
            buf.writeInt(machine.amount);
            ByteBufUtils.writeItemStack(buf, machine.stack);
        }
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerController.class;
    }

    public void handleStoragePull(int id, int flags, EntityPlayerMP player) {
        if (player.inventory.getItemStack() != null) {
            return;
        }

        if (id < 0 || id > itemGroups.size() - 1) {
            return;
        }

        ItemGroup group = itemGroups.get(id);

        int quantity = 64;

        if (GridPullFlags.isPullingHalf(flags) && group.getQuantity() > 1) {
            quantity = group.getQuantity() / 2;

            if (quantity > 32) {
                quantity = 32;
            }
        } else if (GridPullFlags.isPullingOne(flags)) {
            quantity = 1;
        } else if (GridPullFlags.isPullingWithShift(flags)) {
            // NO OP, the quantity already set (64) is needed for shift
        }

        if (quantity > group.getType().getItemStackLimit(group.toStack())) {
            quantity = group.getType().getItemStackLimit(group.toStack());
        }

        ItemStack took = take(group.copy(quantity).toStack());

        if (took != null) {
            if (GridPullFlags.isPullingWithShift(flags)) {
                if (!player.inventory.addItemStackToInventory(took.copy())) {
                    push(took);
                }
            } else {
                player.inventory.setItemStack(took);
                player.updateHeldItem();
            }

            drainEnergyFromWirelessGrid(player, ItemWirelessGrid.USAGE_PULL);
        }
    }

    public void handleStoragePush(int playerSlot, boolean one, EntityPlayerMP player) {
        ItemStack stack;

        if (playerSlot == -1) {
            stack = player.inventory.getItemStack().copy();

            if (one) {
                stack.stackSize = 1;
            }
        } else {
            stack = player.inventory.getStackInSlot(playerSlot);
        }

        if (stack != null) {
            boolean success = push(stack);

            if (success) {
                if (playerSlot == -1) {
                    if (one) {
                        player.inventory.getItemStack().stackSize--;

                        if (player.inventory.getItemStack().stackSize == 0) {
                            player.inventory.setItemStack(null);
                        }
                    } else {
                        player.inventory.setItemStack(null);
                    }

                    player.updateHeldItem();
                } else {
                    player.inventory.setInventorySlotContents(playerSlot, null);
                }
            }

            drainEnergyFromWirelessGrid(player, ItemWirelessGrid.USAGE_PUSH);
        }
    }

    public void sendItemGroups(ByteBuf buf) {
        buf.writeInt(getItemGroups().size());

        for (ItemGroup group : getItemGroups()) {
            group.toBytes(buf, getItemGroups().indexOf(group));
        }
    }

    public void onCraftingRequested(int id, int quantity) {
        if (id >= 0 && id < itemGroups.size() && quantity > 0 && quantity <= MAX_CRAFTING_QUANTITY_PER_REQUEST) {
            ItemStack requested = itemGroups.get(id).toStack();
            int quantityPerRequest = 0;
            CraftingPattern pattern = getPattern(requested);

            if (pattern != null) {
                for (ItemStack output : pattern.getOutputs()) {
                    if (RefinedStorageUtils.compareStackNoQuantity(requested, output)) {
                        quantityPerRequest = output.stackSize;

                        break;
                    }
                }

                while (quantity > 0) {
                    addCraftingTask(pattern);

                    quantity -= quantityPerRequest;
                }
            }
        }
    }

    public void onCraftingTaskCancelRequested(int id) {
        if (id >= 0 && id < craftingTasks.size()) {
            cancelCraftingTask(craftingTasks.get(id));
        } else if (id == -1) {
            for (ICraftingTask task : craftingTasks) {
                cancelCraftingTask(task);
            }
        }
    }
}
