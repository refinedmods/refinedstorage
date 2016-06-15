package refinedstorage.tile.controller;

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
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.api.RefinedStorageCapabilities;
import refinedstorage.api.storage.CompareFlags;
import refinedstorage.api.storage.IStorage;
import refinedstorage.autocrafting.CraftingPattern;
import refinedstorage.autocrafting.task.BasicCraftingTask;
import refinedstorage.autocrafting.task.ICraftingTask;
import refinedstorage.autocrafting.task.ProcessingCraftingTask;
import refinedstorage.block.BlockController;
import refinedstorage.block.EnumControllerType;
import refinedstorage.container.ContainerController;
import refinedstorage.container.ContainerGrid;
import refinedstorage.item.ItemPattern;
import refinedstorage.network.MessageGridItems;
import refinedstorage.tile.*;
import refinedstorage.tile.config.IRedstoneModeConfig;
import refinedstorage.tile.config.RedstoneMode;

import java.util.*;

public class TileController extends TileBase implements IEnergyReceiver, ISynchronizedContainer, IRedstoneModeConfig {
    public static final int ENERGY_CAPACITY = 32000;

    public static final String NBT_CRAFTING_TASKS = "CraftingTasks";
    public static final String NBT_ENERGY = "Energy";

    private EnumControllerType type;

    private StorageHandler storageHandler = new StorageHandler(this);
    private WirelessGridHandler wirelessGridHandler = new WirelessGridHandler(this);

    private List<ItemStack> items = new ArrayList<ItemStack>();
    private List<ItemStack> combinedItems = new ArrayList<ItemStack>();
    private Set<Integer> combinedItemsIndices = new HashSet<Integer>();

    private List<IStorage> storages = new ArrayList<IStorage>();

    private RedstoneMode redstoneMode = RedstoneMode.IGNORE;

    private List<TileMachine> machines = new ArrayList<TileMachine>();
    private List<TileMachine> machinesToAdd = new ArrayList<TileMachine>();
    private List<TileMachine> machinesToRemove = new ArrayList<TileMachine>();

    private List<ClientMachine> clientMachines = new ArrayList<ClientMachine>();

    private List<CraftingPattern> patterns = new ArrayList<CraftingPattern>();

    private Stack<ICraftingTask> craftingTasks = new Stack<ICraftingTask>();
    private List<ICraftingTask> craftingTasksToAddAsLast = new ArrayList<ICraftingTask>();
    private List<ICraftingTask> craftingTasksToAdd = new ArrayList<ICraftingTask>();
    private List<ICraftingTask> craftingTasksToCancel = new ArrayList<ICraftingTask>();

    private EnergyStorage energy = new EnergyStorage(ENERGY_CAPACITY);
    private int energyUsage;

    private int wirelessGridRange;
    private boolean couldRun;
    private long lastEnergyUpdate;

    @Override
    public void update() {
        super.update();

        if (!worldObj.isRemote) {
            for (TileMachine machine : machinesToAdd) {
                if (!machines.contains(machine)) {
                    machines.add(machine);
                }
            }
            machinesToAdd.clear();

            machines.removeAll(machinesToRemove);
            machinesToRemove.clear();

            int lastEnergy = energy.getEnergyStored();

            if (canRun()) {
                if (ticks % 20 == 0) {
                    syncMachines();
                }

                for (ICraftingTask taskToCancel : craftingTasksToCancel) {
                    taskToCancel.onCancelled(this);
                }
                craftingTasks.removeAll(craftingTasksToCancel);
                craftingTasksToCancel.clear();

                for (ICraftingTask task : craftingTasksToAdd) {
                    craftingTasks.push(task);
                }
                craftingTasksToAdd.clear();

                for (ICraftingTask task : craftingTasksToAddAsLast) {
                    craftingTasks.add(0, task);
                }
                craftingTasksToAddAsLast.clear();

                if (!craftingTasks.empty()) {
                    ICraftingTask top = craftingTasks.peek();

                    if (ticks % top.getPattern().getCrafter(worldObj).getSpeed() == 0 && top.update(this)) {
                        top.onDone(this);

                        craftingTasks.pop();
                    }
                }
            } else if (!machines.isEmpty()) {
                // Machine list should NOT be empty to trigger a disconnect
                // We need to sync machines again to reset energy usage etc
                disconnectAll();
                syncMachines();
            }

            if (couldRun != canRun()) {
                couldRun = canRun();

                worldObj.notifyNeighborsOfStateChange(pos, RefinedStorageBlocks.CONTROLLER);
            }

            wirelessGridHandler.update();

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

    public void addMachine(TileMachine machine) {
        machinesToAdd.add(machine);
    }

    public void removeMachine(TileMachine machine) {
        machinesToRemove.add(machine);
    }

    public EnumControllerType getType() {
        if (type == null && worldObj.getBlockState(pos).getBlock() == RefinedStorageBlocks.CONTROLLER) {
            this.type = (EnumControllerType) worldObj.getBlockState(pos).getValue(BlockController.TYPE);
        }

        return type == null ? EnumControllerType.NORMAL : type;
    }

    public StorageHandler getStorageHandler() {
        return storageHandler;
    }

    public WirelessGridHandler getWirelessGridHandler() {
        return wirelessGridHandler;
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

    public List<ItemStack> getItems() {
        return items;
    }

    public List<ICraftingTask> getCraftingTasks() {
        return craftingTasks;
    }

    public void addCraftingTask(ICraftingTask task) {
        craftingTasksToAdd.add(task);

        markDirty();
    }

    public void addCraftingTaskAsLast(ICraftingTask task) {
        craftingTasksToAddAsLast.add(task);

        markDirty();
    }

    public ICraftingTask createCraftingTask(CraftingPattern pattern) {
        if (pattern.isProcessing()) {
            return new ProcessingCraftingTask(pattern);
        } else {
            return new BasicCraftingTask(pattern);
        }
    }

    public void cancelCraftingTask(ICraftingTask task) {
        craftingTasksToCancel.add(task);

        markDirty();
    }

    public List<CraftingPattern> getPatterns() {
        return patterns;
    }

    public List<CraftingPattern> getPattern(ItemStack pattern) {
        return getPattern(pattern, CompareFlags.COMPARE_DAMAGE | CompareFlags.COMPARE_NBT);
    }

    public List<CraftingPattern> getPattern(ItemStack pattern, int flags) {
        List<CraftingPattern> patterns = new ArrayList<CraftingPattern>();

        for (CraftingPattern craftingPattern : getPatterns()) {
            for (ItemStack output : craftingPattern.getOutputs()) {
                if (RefinedStorageUtils.compareStack(output, pattern, flags)) {
                    patterns.add(craftingPattern);
                }
            }
        }

        return patterns;
    }

    public CraftingPattern getPatternWithBestScore(ItemStack pattern) {
        return getPatternWithBestScore(pattern, CompareFlags.COMPARE_DAMAGE | CompareFlags.COMPARE_NBT);
    }

    public CraftingPattern getPatternWithBestScore(ItemStack pattern, int flags) {
        List<CraftingPattern> patterns = getPattern(pattern, flags);

        if (patterns.isEmpty()) {
            return null;
        } else if (patterns.size() == 1) {
            return patterns.get(0);
        }

        int highestScore = 0;
        int highestPattern = 0;

        for (int i = 0; i < patterns.size(); ++i) {
            int score = 0;

            for (ItemStack input : patterns.get(i).getInputs()) {
                ItemStack stored = getItem(input, CompareFlags.COMPARE_DAMAGE | CompareFlags.COMPARE_NBT);

                score += stored != null ? stored.stackSize : 0;
            }

            if (score > highestScore) {
                highestScore = score;
                highestPattern = i;
            }
        }

        return patterns.get(highestPattern);
    }

    private void syncMachines() {
        this.wirelessGridRange = 0;
        this.energyUsage = 0;
        this.storages.clear();
        this.patterns.clear();

        for (TileMachine machine : machines) {
            if (!machine.canUpdate()) {
                continue;
            }

            if (machine instanceof TileWirelessTransmitter) {
                this.wirelessGridRange += ((TileWirelessTransmitter) machine).getRange();
            }

            if (machine.hasCapability(RefinedStorageCapabilities.STORAGE_PROVIDER_CAPABILITY, null)) {
                machine.getCapability(RefinedStorageCapabilities.STORAGE_PROVIDER_CAPABILITY, null).provide(storages);
            }

            if (machine instanceof TileCrafter) {
                TileCrafter crafter = (TileCrafter) machine;

                for (int i = 0; i < crafter.getPatterns().getSlots(); ++i) {
                    ItemStack pattern = crafter.getPatterns().getStackInSlot(i);

                    if (pattern != null && ItemPattern.isValid(pattern)) {
                        patterns.add(new CraftingPattern(crafter.getPos().getX(), crafter.getPos().getY(), crafter.getPos().getZ(), ItemPattern.isProcessing(pattern), ItemPattern.getInputs(pattern), ItemPattern.getOutputs(pattern), ItemPattern.getByproducts(pattern)));
                    }
                }
            }

            this.energyUsage += machine.getEnergyUsage();
        }

        Collections.sort(storages, new Comparator<IStorage>() {
            @Override
            public int compare(IStorage left, IStorage right) {
                int leftStored = left.getStored();
                int rightStored = right.getStored();

                if (leftStored == rightStored) {
                    return 0;
                }

                return (leftStored > rightStored) ? -1 : 1;
            }
        });

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
        syncItemsWithClients();
    }

    private void syncItems() {
        items.clear();

        for (IStorage storage : storages) {
            storage.addItems(items);
        }

        for (CraftingPattern pattern : patterns) {
            for (ItemStack output : pattern.getOutputs()) {
                ItemStack patternStack = output.copy();
                patternStack.stackSize = 0;
                items.add(patternStack);
            }
        }

        combinedItems.clear();
        combinedItemsIndices.clear();

        for (int i = 0; i < items.size(); ++i) {
            if (combinedItemsIndices.contains(i)) {
                continue;
            }

            ItemStack stack = items.get(i);

            for (int j = i + 1; j < items.size(); ++j) {
                if (combinedItemsIndices.contains(j)) {
                    continue;
                }

                ItemStack otherStack = items.get(j);

                if (RefinedStorageUtils.compareStackNoQuantity(stack, otherStack)) {
                    // We copy here so we don't modify the quantity of the ItemStack IStorage uses.
                    // We re-get the ItemStack because the stack may change from a previous iteration in this loop
                    ItemStack newStack = items.get(i).copy();
                    newStack.stackSize += otherStack.stackSize;
                    items.set(i, newStack);

                    combinedItems.add(otherStack);
                    combinedItemsIndices.add(j);
                }
            }
        }

        items.removeAll(combinedItems);
    }

    public void syncItemsWithClients() {
        for (EntityPlayer player : worldObj.playerEntities) {
            if (player.openContainer.getClass() == ContainerGrid.class && pos.equals(((ContainerGrid) player.openContainer).getGrid().getControllerPos())) {
                syncItemsWithClient((EntityPlayerMP) player);
            }
        }
    }

    public void syncItemsWithClient(EntityPlayerMP player) {
        RefinedStorage.NETWORK.sendTo(new MessageGridItems(this), player);
    }

    public ItemStack push(ItemStack stack, int size, boolean simulate) {
        if (stack == null || stack.getItem() == null) {
            return null;
        }

        if (storages.isEmpty()) {
            return ItemHandlerHelper.copyStackWithSize(stack, size);
        }

        int orginalSize = size;

        ItemStack remainder = stack;

        for (IStorage storage : storages) {
            remainder = storage.push(remainder, size, simulate);

            if (remainder == null) {
                break;
            } else {
                size = remainder.stackSize;
            }
        }

        if (!simulate) {
            syncItems();
            syncItemsWithClients();

            int sizePushed = remainder != null ? (orginalSize - remainder.stackSize) : orginalSize;

            for (int i = 0; i < sizePushed; ++i) {
                if (!craftingTasks.empty()) {
                    ICraftingTask top = craftingTasks.peek();

                    if (top instanceof ProcessingCraftingTask) {
                        ((ProcessingCraftingTask) top).onPushed(stack);
                    }
                }
            }
        }

        return remainder;
    }

    public ItemStack take(ItemStack stack, int size) {
        return take(stack, size, CompareFlags.COMPARE_DAMAGE | CompareFlags.COMPARE_NBT);
    }

    public ItemStack take(ItemStack stack, int size, int flags) {
        int requested = size;
        int received = 0;

        ItemStack newStack = null;

        for (IStorage storage : storages) {
            ItemStack took = storage.take(stack, requested - received, flags);

            if (took != null) {
                if (newStack == null) {
                    newStack = took;
                } else {
                    newStack.stackSize += took.stackSize;
                }

                received += took.stackSize;
            }

            if (requested == received) {
                break;
            }
        }

        if (newStack != null) {
            syncItems();
            syncItemsWithClients();
        }

        return newStack;
    }

    public ItemStack getItem(ItemStack stack, int flags) {
        for (ItemStack otherStack : items) {
            if (RefinedStorageUtils.compareStack(otherStack, stack, flags)) {
                return otherStack;
            }
        }

        return null;
    }

    public void setEnergyStored(int amount) {
        energy.setEnergyStored(amount);
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        energy.writeToNBT(tag);

        tag.setInteger(RedstoneMode.NBT, redstoneMode.id);

        NBTTagList list = new NBTTagList();

        for (ICraftingTask task : craftingTasks) {
            NBTTagCompound taskTag = new NBTTagCompound();
            task.writeToNBT(taskTag);
            list.appendTag(taskTag);
        }

        tag.setTag(NBT_CRAFTING_TASKS, list);

        return tag;
    }


    @Override
    public void read(NBTTagCompound nbt) {
        super.read(nbt);

        energy.readFromNBT(nbt);

        if (nbt.hasKey(RedstoneMode.NBT)) {
            redstoneMode = RedstoneMode.getById(nbt.getInteger(RedstoneMode.NBT));
        }

        if (nbt.hasKey(NBT_CRAFTING_TASKS)) {
            NBTTagList taskList = nbt.getTagList(NBT_CRAFTING_TASKS, Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < taskList.tagCount(); ++i) {
                NBTTagCompound taskTag = taskList.getCompoundTagAt(i);

                CraftingPattern pattern = CraftingPattern.readFromNBT(taskTag.getCompoundTag(CraftingPattern.NBT));

                if (pattern != null) {
                    switch (taskTag.getInteger("Type")) {
                        case BasicCraftingTask.ID:
                            addCraftingTask(new BasicCraftingTask(taskTag, pattern));
                            break;
                        case ProcessingCraftingTask.ID:
                            addCraftingTask(new ProcessingCraftingTask(taskTag, pattern));
                            break;
                    }
                }
            }
        }
    }

    @Override
    public NBTTagCompound writeUpdate(NBTTagCompound tag) {
        super.writeUpdate(tag);

        tag.setInteger(NBT_ENERGY, getEnergyStored(null));

        return tag;
    }

    @Override
    public void readUpdate(NBTTagCompound tag) {
        setEnergyStored(tag.getInteger(NBT_ENERGY));

        super.readUpdate(tag);
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
        return energy.getEnergyStored() > 0 && energy.getEnergyStored() >= energyUsage && redstoneMode.isEnabled(worldObj, pos);
    }

    @Override
    public RedstoneMode getRedstoneMode() {
        return redstoneMode;
    }

    @Override
    public void setRedstoneMode(RedstoneMode mode) {
        this.redstoneMode = mode;

        markDirty();
    }

    public List<ClientMachine> getClientMachines() {
        return clientMachines;
    }

    @Override
    public void readContainerData(ByteBuf buf) {
        setEnergyStored(buf.readInt());
        energyUsage = buf.readInt();

        redstoneMode = RedstoneMode.getById(buf.readInt());

        machines.clear();

        List<ClientMachine> machines = new ArrayList<ClientMachine>();

        int size = buf.readInt();

        for (int i = 0; i < size; ++i) {
            ClientMachine machine = new ClientMachine();
            machine.energyUsage = buf.readInt();
            machine.amount = buf.readInt();
            machine.stack = ByteBufUtils.readItemStack(buf);

            machines.add(machine);
        }

        clientMachines = machines;
    }

    @Override
    public void writeContainerData(ByteBuf buf) {
        buf.writeInt(getEnergyStored(null));
        buf.writeInt(energyUsage);

        buf.writeInt(redstoneMode.id);

        List<ClientMachine> m = new ArrayList<ClientMachine>();

        for (TileMachine machine : machines) {
            if (machine.canUpdate()) {
                IBlockState state = worldObj.getBlockState(machine.getPos());

                ClientMachine clientMachine = new ClientMachine();

                clientMachine.energyUsage = machine.getEnergyUsage();
                clientMachine.amount = 1;
                clientMachine.stack = new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state));

                if (m.contains(clientMachine)) {
                    for (ClientMachine other : m) {
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

        for (ClientMachine machine : m) {
            buf.writeInt(machine.energyUsage);
            buf.writeInt(machine.amount);
            ByteBufUtils.writeItemStack(buf, machine.stack);
        }
    }

    @Override
    public Class<? extends Container> getContainer() {
        return ContainerController.class;
    }
}
