package refinedstorage.api.network;

import cofh.api.energy.EnergyStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.RefinedStorageUtils;
import refinedstorage.api.RefinedStorageCapabilities;
import refinedstorage.api.storage.CompareFlags;
import refinedstorage.api.storage.IStorage;
import refinedstorage.api.storage.IStorageProvider;
import refinedstorage.autocrafting.CraftingPattern;
import refinedstorage.autocrafting.task.BasicCraftingTask;
import refinedstorage.autocrafting.task.ICraftingTask;
import refinedstorage.autocrafting.task.ProcessingCraftingTask;
import refinedstorage.block.BlockController;
import refinedstorage.block.EnumControllerType;
import refinedstorage.container.ContainerGrid;
import refinedstorage.item.ItemPattern;
import refinedstorage.network.MessageGridItems;
import refinedstorage.tile.TileCrafter;
import refinedstorage.tile.TileWirelessTransmitter;
import refinedstorage.tile.config.RedstoneMode;
import refinedstorage.tile.controller.StorageHandler;
import refinedstorage.tile.controller.WirelessGridHandler;

import java.util.*;

public class NetworkMaster {
    public static final int ENERGY_CAPACITY = 32000;

    public static final String NBT_CRAFTING_TASKS = "CraftingTasks";
    public static final String NBT_ENERGY = "Energy";
    public static final String NBT_SLAVES = "Slaves";
    public static final String NBT_SLAVE_X = "X";
    public static final String NBT_SLAVE_Y = "Y";
    public static final String NBT_SLAVE_Z = "Z";

    private StorageHandler storageHandler = new StorageHandler(this);
    private WirelessGridHandler wirelessGridHandler = new WirelessGridHandler(this);

    private List<ItemStack> items = new ArrayList<ItemStack>();
    private List<ItemStack> combinedItems = new ArrayList<ItemStack>();
    private Set<Integer> combinedItemsIndices = new HashSet<Integer>();

    private List<IStorage> storages = new ArrayList<IStorage>();

    private List<BlockPos> slaves = new ArrayList<BlockPos>();
    private List<BlockPos> slavesToAdd = new ArrayList<BlockPos>();
    private List<BlockPos> slavesToLoad = new ArrayList<BlockPos>();
    private List<BlockPos> slavesToRemove = new ArrayList<BlockPos>();

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

    private int ticks;

    private EnumControllerType type;

    private World world;
    private BlockPos pos;

    private RedstoneMode redstoneMode = RedstoneMode.IGNORE;

    public NetworkMaster(BlockPos pos, World world) {
        this.pos = pos;

        onAdded(world);
    }

    public NetworkMaster(BlockPos pos) {
        this.pos = pos;
    }

    public RedstoneMode getRedstoneMode() {
        return redstoneMode;
    }

    public void setRedstoneMode(RedstoneMode mode) {
        this.redstoneMode = mode;

        markDirty();
    }

    public EnergyStorage getEnergy() {
        return energy;
    }

    public int getEnergyUsage() {
        return energyUsage;
    }

    public BlockPos getPosition() {
        return pos;
    }

    public World getWorld() {
        return world;
    }

    public boolean canRun() {
        return energy.getEnergyStored() > 0 && energy.getEnergyStored() >= energyUsage && redstoneMode.isEnabled(world, pos);
    }

    public void update() {
        for (BlockPos slave : slavesToAdd) {
            if (!slaves.contains(slave)) {
                slaves.add(slave);
            }
        }
        slavesToAdd.clear();

        slaves.removeAll(slavesToRemove);
        slavesToRemove.clear();

        int lastEnergy = energy.getEnergyStored();

        if (canRun()) {
            if (ticks % 20 == 0) {
                syncMachines();
            }

            Iterator<INetworkSlave> slaves = getSlaves();
            while (slaves.hasNext()) {
                INetworkSlave slave = slaves.next();

                if (slave.canUpdate()) {
                    slave.updateSlave();
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

                if (ticks % top.getPattern().getCrafter(world).getSpeed() == 0 && top.update(this)) {
                    top.onDone(this);

                    craftingTasks.pop();
                }
            }
        } else if (!slaves.isEmpty()) {
            disconnectAll();
            syncMachines();
        }

        if (couldRun != canRun()) {
            couldRun = canRun();

            world.notifyNeighborsOfStateChange(pos, RefinedStorageBlocks.CONTROLLER);
        }

        wirelessGridHandler.update();

        if (type == EnumControllerType.NORMAL && energyUsage > 0) {
            if (energy.getEnergyStored() - energyUsage >= 0) {
                energy.extractEnergy(energyUsage, false);
            } else {
                energy.setEnergyStored(0);
            }
        } else if (type == EnumControllerType.CREATIVE) {
            energy.setEnergyStored(energy.getMaxEnergyStored());
        }

        if (energy.getEnergyStored() != lastEnergy) {
            world.updateComparatorOutputLevel(pos, RefinedStorageBlocks.CONTROLLER);

            if (System.currentTimeMillis() - lastEnergyUpdate > 5000) {
                lastEnergyUpdate = System.currentTimeMillis();

                RefinedStorageUtils.updateBlock(world, pos);
            }
        }

        ticks++;
    }

    public Iterator<INetworkSlave> getSlaves() {
        return new Iterator<INetworkSlave>() {
            private int index;

            @Override
            public boolean hasNext() {
                return index < slaves.size();
            }

            @Override
            public INetworkSlave next() {
                return world.getTileEntity(slaves.get(index++)).getCapability(RefinedStorageCapabilities.NETWORK_SLAVE_CAPABILITY, null);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public void addSlave(BlockPos slave) {
        slavesToAdd.add(slave);

        markDirty();
    }

    public void removeSlave(BlockPos slave) {
        slavesToRemove.add(slave);

        markDirty();
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

    public void disconnectAll() {
        Iterator<INetworkSlave> slaves = getSlaves();

        while (slaves.hasNext()) {
            slaves.next().disconnect(world);
        }

        this.slaves.clear();
    }

    public void onRemoved() {
        markDirty();
    }

    public void onAdded(World world) {
        this.world = world;
        this.type = (EnumControllerType) world.getBlockState(pos).getValue(BlockController.TYPE);

        for (BlockPos slavePos : slavesToLoad) {
            INetworkSlave slave = world.getTileEntity(slavePos).getCapability(RefinedStorageCapabilities.NETWORK_SLAVE_CAPABILITY, null);

            slave.forceConnect(this);

            slaves.add(slavePos);
        }

        this.slavesToLoad.clear();
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

        Iterator<INetworkSlave> slaves = getSlaves();
        while (slaves.hasNext()) {
            INetworkSlave slave = slaves.next();

            if (!slave.canUpdate()) {
                continue;
            }

            if (slave instanceof TileWirelessTransmitter) {
                this.wirelessGridRange += ((TileWirelessTransmitter) slave).getRange();
            }

            if (slave instanceof IStorageProvider) {
                ((IStorageProvider) slave).provide(storages);
            }

            if (slave instanceof TileCrafter) {
                TileCrafter crafter = (TileCrafter) slave;

                for (int i = 0; i < crafter.getPatterns().getSlots(); ++i) {
                    ItemStack pattern = crafter.getPatterns().getStackInSlot(i);

                    if (pattern != null && ItemPattern.isValid(pattern)) {
                        patterns.add(new CraftingPattern(crafter.getPos().getX(), crafter.getPos().getY(), crafter.getPos().getZ(), ItemPattern.isProcessing(pattern), ItemPattern.getInputs(pattern), ItemPattern.getOutputs(pattern), ItemPattern.getByproducts(pattern)));
                    }
                }
            }

            this.energyUsage += slave.getEnergyUsage();
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
        for (EntityPlayer player : world.playerEntities) {
            if (player.openContainer.getClass() == ContainerGrid.class && pos.equals(((ContainerGrid) player.openContainer).getGrid().getNetworkPosition())) {
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

    public void readFromNBT(NBTTagCompound tag) {
        energy.readFromNBT(tag);

        if (tag.hasKey(RedstoneMode.NBT)) {
            redstoneMode = RedstoneMode.getById(tag.getInteger(RedstoneMode.NBT));
        }

        if (tag.hasKey(NBT_CRAFTING_TASKS)) {
            NBTTagList taskList = tag.getTagList(NBT_CRAFTING_TASKS, Constants.NBT.TAG_COMPOUND);

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

        if (tag.hasKey(NBT_SLAVES)) {
            NBTTagList slavesTag = tag.getTagList(NBT_SLAVES, Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < slavesTag.tagCount(); ++i) {
                NBTTagCompound slave = slavesTag.getCompoundTagAt(i);

                slavesToLoad.add(new BlockPos(slave.getInteger(NBT_SLAVE_X), slave.getInteger(NBT_SLAVE_Y), slave.getInteger(NBT_SLAVE_Z)));
            }
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        energy.writeToNBT(tag);

        tag.setInteger(RedstoneMode.NBT, redstoneMode.id);

        NBTTagList list = new NBTTagList();

        for (ICraftingTask task : craftingTasks) {
            NBTTagCompound taskTag = new NBTTagCompound();
            task.writeToNBT(taskTag);
            list.appendTag(taskTag);
        }

        tag.setTag(NBT_CRAFTING_TASKS, list);

        NBTTagList slavesTag = new NBTTagList();

        for (BlockPos slave : slaves) {
            NBTTagCompound slaveTag = new NBTTagCompound();

            slaveTag.setInteger(NBT_SLAVE_X, slave.getX());
            slaveTag.setInteger(NBT_SLAVE_Y, slave.getY());
            slaveTag.setInteger(NBT_SLAVE_Z, slave.getZ());

            slavesTag.appendTag(slaveTag);
        }

        tag.setTag(NBT_SLAVES, slavesTag);

        return tag;
    }

    public void markDirty() {
        NetworkMasterSavedData.getOrLoad(world).markDirty();
    }
}
