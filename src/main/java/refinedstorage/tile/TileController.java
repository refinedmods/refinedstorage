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
import refinedstorage.network.MessageControllerEnergyUpdate;
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
        public int energyUsage;
        public int x;
        public int y;
        public int z;
    }

    public static final int ENERGY_CAPACITY = 32000;

    public static final String NBT_CRAFTING_TASKS = "CraftingTasks";

    private List<ItemGroup> itemGroups = new ArrayList<ItemGroup>();
    private List<IStorage> storages = new ArrayList<IStorage>();
    private List<WirelessGridConsumer> wirelessGridConsumers = new ArrayList<WirelessGridConsumer>();
    private List<WirelessGridConsumer> wirelessGridConsumersToRemove = new ArrayList<WirelessGridConsumer>();

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

    private int wirelessGridRange;

    private long lastEnergyUpdate;

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
            machines.addAll(machinesToAdd);
            machinesToAdd.clear();

            machines.removeAll(machinesToRemove);
            machinesToRemove.clear();

            int lastEnergy = energy.getEnergyStored();

            int newWirelessGridRange = 0;
            int newEnergyUsage = 10;
            List<IStorage> newStorages = new ArrayList<IStorage>();
            List<CraftingPattern> newPatterns = new ArrayList<CraftingPattern>();

            for (TileMachine machine : machines) {
                machine.updateMachine();

                if (machine instanceof TileWirelessTransmitter) {
                    newWirelessGridRange += ((TileWirelessTransmitter) machine).getRange();
                }

                if (machine instanceof IStorageProvider) {
                    ((IStorageProvider) machine).provide(newStorages);
                }

                if (machine instanceof TileCrafter) {
                    TileCrafter crafter = (TileCrafter) machine;

                    for (int i = 0; i < TileCrafter.PATTERN_SLOTS; ++i) {
                        if (crafter.getStackInSlot(i) != null) {
                            ItemStack pattern = crafter.getStackInSlot(i);

                            newPatterns.add(new CraftingPattern(
                                crafter.getPos().getX(),
                                crafter.getPos().getY(),
                                crafter.getPos().getZ(),
                                ItemPattern.isProcessing(pattern),
                                ItemPattern.getInputs(pattern),
                                ItemPattern.getOutputs(pattern)));
                        }
                    }
                }

                newEnergyUsage += machine.getEnergyUsage();
            }

            wirelessGridRange = newWirelessGridRange;
            energyUsage = newEnergyUsage;
            storages = newStorages;
            patterns = newPatterns;

            Collections.sort(storages, new Comparator<IStorage>() {
                @Override
                public int compare(IStorage s1, IStorage s2) {
                    if (s1.getPriority() == s2.getPriority()) {
                        return 0;
                    }

                    return (s1.getPriority() > s2.getPriority()) ? -1 : 1;
                }
            });

            syncItems();

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

            switch (getType()) {
                case NORMAL:
                    if (canRun()) {
                        energy.extractEnergy(energyUsage, false);
                    }
                    break;
                case CREATIVE:
                    energy.setEnergyStored(energy.getMaxEnergyStored());
                    break;
            }

            wirelessGridConsumers.removeAll(wirelessGridConsumersToRemove);
            wirelessGridConsumersToRemove.clear();

            Iterator<WirelessGridConsumer> gridConsumerIterator = wirelessGridConsumers.iterator();

            while (gridConsumerIterator.hasNext()) {
                WirelessGridConsumer consumer = gridConsumerIterator.next();

                if (!RefinedStorageUtils.compareStack(consumer.getWirelessGrid(), consumer.getPlayer().getHeldItem(consumer.getHand()))) {
                    consumer.getPlayer().closeScreen(); // This will call onContainerClosed on the Container and remove it from the list
                } else {
                    if (canRun()) {
                        RefinedStorage.NETWORK.sendTo(new MessageWirelessGridItems(this), (EntityPlayerMP) consumer.getPlayer());
                    }
                }
            }

            if (lastEnergy != energy.getEnergyStored()) {
                worldObj.updateComparatorOutputLevel(pos, RefinedStorageBlocks.CONTROLLER);

                if (System.currentTimeMillis() - lastEnergyUpdate > 3000) {
                    RefinedStorageUtils.sendToAllAround(worldObj, pos, new MessageControllerEnergyUpdate(this));

                    lastEnergyUpdate = System.currentTimeMillis();
                }
            }
        }
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

    public void onDestroyed() {
        disconnectAll();
    }

    private void disconnectAll() {
        for (TileMachine machine : machines) {
            machine.onDisconnected();
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

        combineItems();
    }

    private void combineItems() {
        List<Integer> markedIndexes = new ArrayList<Integer>();

        for (int i = 0; i < itemGroups.size(); ++i) {
            if (markedIndexes.contains(i)) {
                continue;
            }

            ItemGroup group = itemGroups.get(i);

            // If the item doesn't exist anymore, remove it from storage to avoid crashes
            if (group.getType() == null) {
                markedIndexes.add(i);
            } else {
                for (int j = i + 1; j < itemGroups.size(); ++j) {
                    if (markedIndexes.contains(j)) {
                        continue;
                    }

                    ItemGroup otherGroup = itemGroups.get(j);

                    if (group.compareNoQuantity(otherGroup)) {
                        group.setQuantity(group.getQuantity() + otherGroup.getQuantity());

                        markedIndexes.add(j);
                    }
                }
            }
        }

        List<ItemGroup> markedItems = new ArrayList<ItemGroup>();

        for (int i : markedIndexes) {
            markedItems.add(itemGroups.get(i));
        }

        itemGroups.removeAll(markedItems);
    }

    public boolean push(ItemStack stack) {
        for (IStorage storage : storages) {
            if (storage.canPush(stack)) {
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

                markDirty();

                return true;
            }
        }

        return false;
    }

    public ItemStack take(ItemStack stack) {
        markDirty();

        return take(stack, RefinedStorageUtils.COMPARE_DAMAGE | RefinedStorageUtils.COMPARE_NBT);
    }

    public ItemStack take(ItemStack stack, int flags) {
        markDirty();

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

        syncItems();

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

    public boolean canRun() {
        return energy.getEnergyStored() >= getEnergyUsage() && redstoneMode.isEnabled(worldObj, pos);
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
        energyUsage = buf.readInt();

        redstoneMode = RedstoneMode.getById(buf.readInt());

        machines.clear();

        List<ClientSideMachine> machines = new ArrayList<ClientSideMachine>();

        int size = buf.readInt();

        for (int i = 0; i < size; ++i) {
            int energyUsage = buf.readInt();
            int x = buf.readInt();
            int y = buf.readInt();
            int z = buf.readInt();
            ItemStack stack = ByteBufUtils.readItemStack(buf);

            ClientSideMachine machine = new ClientSideMachine();
            machine.x = x;
            machine.y = y;
            machine.z = z;
            machine.energyUsage = energyUsage;
            machine.stack = stack;

            machines.add(machine);
        }

        clientSideMachines = machines;
    }

    @Override
    public void sendContainerData(ByteBuf buf) {
        buf.writeInt(canRun() ? energyUsage : 0);

        buf.writeInt(redstoneMode.id);

        buf.writeInt(machines.size());

        for (TileMachine machine : machines) {
            buf.writeInt(machine.getEnergyUsage());

            buf.writeInt(machine.getPos().getX());
            buf.writeInt(machine.getPos().getY());
            buf.writeInt(machine.getPos().getZ());

            IBlockState state = worldObj.getBlockState(machine.getPos());

            ByteBufUtils.writeItemStack(buf, new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state)));
        }
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerController.class;
    }

    public void handleStoragePull(int id, int flags, EntityPlayerMP player) {
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

        if (quantity > group.getType().getItemStackLimit(group.toItemStack())) {
            quantity = group.getType().getItemStackLimit(group.toItemStack());
        }

        ItemStack took = take(group.copy(quantity).toItemStack());

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
        if (id >= 0 && id < itemGroups.size() && quantity > 0) {
            ItemStack requested = itemGroups.get(id).toItemStack();
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
