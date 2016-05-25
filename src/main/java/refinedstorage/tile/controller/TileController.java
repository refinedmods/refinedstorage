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
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.block.BlockController;
import refinedstorage.block.EnumControllerType;
import refinedstorage.container.ContainerController;
import refinedstorage.container.ContainerGrid;
import refinedstorage.item.ItemPattern;
import refinedstorage.network.MessageGridItems;
import refinedstorage.storage.IStorage;
import refinedstorage.storage.IStorageProvider;
import refinedstorage.storage.ItemGroup;
import refinedstorage.tile.ISynchronizedContainer;
import refinedstorage.tile.TileBase;
import refinedstorage.tile.TileMachine;
import refinedstorage.tile.TileWirelessTransmitter;
import refinedstorage.tile.autocrafting.CraftingPattern;
import refinedstorage.tile.autocrafting.TileCrafter;
import refinedstorage.tile.autocrafting.task.BasicCraftingTask;
import refinedstorage.tile.autocrafting.task.ICraftingTask;
import refinedstorage.tile.autocrafting.task.ProcessingCraftingTask;
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

    private List<ItemGroup> itemGroups = new ArrayList<ItemGroup>();

    private List<IStorage> storages = new ArrayList<IStorage>();

    private Set<Integer> combinedGroupsIndices = new HashSet<Integer>();
    private List<ItemGroup> combinedGroups = new ArrayList<ItemGroup>();

    private RedstoneMode redstoneMode = RedstoneMode.IGNORE;

    private List<TileMachine> machines = new ArrayList<TileMachine>();
    private List<TileMachine> machinesToAdd = new ArrayList<TileMachine>();
    private List<TileMachine> machinesToRemove = new ArrayList<TileMachine>();

    private List<ClientSideMachine> clientSideMachines = new ArrayList<ClientSideMachine>();

    private List<CraftingPattern> patterns = new ArrayList<CraftingPattern>();
    private Stack<ICraftingTask> craftingTasks = new Stack<ICraftingTask>();
    private List<ICraftingTask> craftingTasksToAddAsLast = new ArrayList<ICraftingTask>();
    private List<ICraftingTask> craftingTasksToAdd = new ArrayList<ICraftingTask>();
    private List<ICraftingTask> craftingTasksToCancel = new ArrayList<ICraftingTask>();

    private EnergyStorage energy = new EnergyStorage(ENERGY_CAPACITY);
    private int energyUsage;

    private int wirelessGridRange;

    private boolean couldRun;
    private boolean syncing;

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
            } else {
                disconnectAll();
            }

            if (couldRun != mayRun()) {
                couldRun = mayRun();

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

    public List<ItemGroup> getItemGroups() {
        return itemGroups;
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

    private void syncMachines() {
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
                    ItemStack pattern = crafter.getPatterns().getStackInSlot(i);

                    if (pattern != null && ItemPattern.isValid(pattern)) {
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
        syncItemsWithClients();
    }

    private void syncItems() {
        this.syncing = true;

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
                        // We copy here so we don't modify the quantity of the item group IStorage uses.
                        // We re-get the itemgroup with .get(i) because the group may change from a previous iteration in this for loop.
                        itemGroups.set(i, itemGroups.get(i).copy(itemGroups.get(i).getQuantity() + otherGroup.getQuantity()));

                        combinedGroups.add(otherGroup);
                        combinedGroupsIndices.add(j);
                    }
                }
            }
        }

        itemGroups.removeAll(combinedGroups);

        this.syncing = false;
    }

    public void syncItemsWithClients() {
        if (!syncing) {
            for (EntityPlayer player : worldObj.playerEntities) {
                if (player.openContainer.getClass() == ContainerGrid.class) {
                    syncItemsWithClient((EntityPlayerMP) player);
                }
            }
        }
    }

    public void syncItemsWithClient(EntityPlayerMP player) {
        RefinedStorage.NETWORK.sendTo(new MessageGridItems(this), player);
    }

    public boolean push(ItemStack stack) {
        for (IStorage storage : storages) {
            if (storage.mayPush(stack)) {
                storage.push(stack);

                syncItems();
                syncItemsWithClients();

                for (int i = 0; i < stack.stackSize; ++i) {
                    if (!craftingTasks.empty()) {
                        ICraftingTask top = craftingTasks.peek();

                        if (top instanceof ProcessingCraftingTask) {
                            ((ProcessingCraftingTask) top).onPushed(stack);
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
            syncItemsWithClients();
        }

        return newStack;
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
        super.readUpdate(tag);

        setEnergyStored(tag.getInteger(NBT_ENERGY));
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
        this.redstoneMode = mode;

        markDirty();
    }

    @Override
    public BlockPos getMachinePos() {
        return pos;
    }

    public List<ClientSideMachine> getClientSideMachines() {
        return clientSideMachines;
    }

    @Override
    public void readContainerData(ByteBuf buf) {
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
    public void writeContainerData(ByteBuf buf) {
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
}
