package refinedstorage.apiimpl.network;

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
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.ICraftingTask;
import refinedstorage.api.network.IGridHandler;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.network.INetworkSlave;
import refinedstorage.api.network.IWirelessGridHandler;
import refinedstorage.api.storage.CompareFlags;
import refinedstorage.api.storage.IStorage;
import refinedstorage.api.storage.IStorageProvider;
import refinedstorage.apiimpl.autocrafting.BasicCraftingTask;
import refinedstorage.apiimpl.autocrafting.CraftingPattern;
import refinedstorage.apiimpl.autocrafting.ProcessingCraftingTask;
import refinedstorage.block.BlockController;
import refinedstorage.block.EnumControllerType;
import refinedstorage.container.ContainerGrid;
import refinedstorage.item.ItemPattern;
import refinedstorage.network.MessageGridItems;
import refinedstorage.tile.TileCable;
import refinedstorage.tile.TileCrafter;
import refinedstorage.tile.TileWirelessTransmitter;
import refinedstorage.tile.config.RedstoneMode;

import java.util.*;

public class NetworkMaster implements INetworkMaster {
    public static final int ENERGY_CAPACITY = 32000;

    public static final String NBT_CRAFTING_TASKS = "CraftingTasks";
    public static final String NBT_ENERGY = "Energy";
    public static final String NBT_SLAVES = "Slaves";
    public static final String NBT_SLAVE_X = "X";
    public static final String NBT_SLAVE_Y = "Y";
    public static final String NBT_SLAVE_Z = "Z";

    private GridHandler gridHandler = new GridHandler(this);
    private WirelessGridHandler wirelessGridHandler = new WirelessGridHandler(this);

    private List<ItemStack> items = new ArrayList<ItemStack>();
    private List<ItemStack> combinedItems = new ArrayList<ItemStack>();
    private Set<Integer> combinedItemsIndices = new HashSet<Integer>();

    private List<IStorage> storages = new ArrayList<IStorage>();

    private List<BlockPos> slaves = new ArrayList<BlockPos>();
    private Map<BlockPos, Boolean> slaveConnectivity = new HashMap<BlockPos, Boolean>();
    private List<BlockPos> slavesToAdd = new ArrayList<BlockPos>();
    private List<BlockPos> slavesToLoad = new ArrayList<BlockPos>();
    private List<BlockPos> slavesToRemove = new ArrayList<BlockPos>();

    private List<ICraftingPattern> patterns = new ArrayList<ICraftingPattern>();

    private Stack<ICraftingTask> craftingTasks = new Stack<ICraftingTask>();
    private List<ICraftingTask> craftingTasksToAddAsLast = new ArrayList<ICraftingTask>();
    private List<ICraftingTask> craftingTasksToAdd = new ArrayList<ICraftingTask>();
    private List<ICraftingTask> craftingTasksToCancel = new ArrayList<ICraftingTask>();

    private EnergyStorage energy = new EnergyStorage(ENERGY_CAPACITY);
    private int energyUsage;

    private boolean couldRun;
    private long lastEnergyUpdate;

    private int ticks;

    private EnumControllerType type;

    private World world;
    private BlockPos pos;

    private RedstoneMode redstoneMode = RedstoneMode.IGNORE;

    public NetworkMaster(BlockPos pos, World world) {
        this.pos = pos;

        setWorld(world);
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

    @Override
    public EnergyStorage getEnergy() {
        return energy;
    }

    @Override
    public int getEnergyUsage() {
        return energyUsage;
    }

    @Override
    public BlockPos getPosition() {
        return pos;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public void setWorld(World world) {
        this.world = world;
        this.type = (EnumControllerType) world.getBlockState(pos).getValue(BlockController.TYPE);

        for (BlockPos slavePos : slavesToLoad) {
            INetworkSlave slave = world.getTileEntity(slavePos).getCapability(RefinedStorageCapabilities.NETWORK_SLAVE_CAPABILITY, null);

            slave.forceConnect(this);

            if (!(slave instanceof TileCable)) {
                slaves.add(slavePos);
            }
        }

        this.slavesToLoad.clear();
    }

    @Override
    public boolean canRun() {
        return energy.getEnergyStored() > 0 && energy.getEnergyStored() >= energyUsage && redstoneMode.isEnabled(world, pos);
    }

    @Override
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
                updateSlaves();
            }

            Iterator<INetworkSlave> slaves = getSlaves();
            while (slaves.hasNext()) {
                INetworkSlave slave = slaves.next();

                if (slave.canUpdate()) {
                    slave.updateSlave();
                }

                boolean active = slave.canUpdate();

                if (!slaveConnectivity.containsKey(slave.getPosition()) || slaveConnectivity.get(slave.getPosition()) != active) {
                    slaveConnectivity.put(slave.getPosition(), active);

                    if (slave.canSendConnectivityUpdate()) {
                        RefinedStorageUtils.updateBlock(world, slave.getPosition());
                    }
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

                if (ticks % top.getPattern().getContainer(world).getSpeed() == 0 && top.update(this)) {
                    top.onDone(this);

                    craftingTasks.pop();
                }
            }
        } else if (!slaves.isEmpty()) {
            disconnectAll();
            updateSlaves();
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

            if (System.currentTimeMillis() - lastEnergyUpdate > 1500) {
                lastEnergyUpdate = System.currentTimeMillis();

                RefinedStorageUtils.updateBlock(world, pos);
            }
        }

        ticks++;
    }

    @Override
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

    @Override
    public void addSlave(BlockPos slave) {
        slavesToAdd.add(slave);

        markDirty();
    }

    @Override
    public void removeSlave(BlockPos slave) {
        slavesToRemove.add(slave);

        markDirty();
    }

    @Override
    public IGridHandler getGridHandler() {
        return gridHandler;
    }

    @Override
    public IWirelessGridHandler getWirelessGridHandler() {
        return wirelessGridHandler;
    }

    public void disconnectAll() {
        Iterator<INetworkSlave> slaves = getSlaves();

        while (slaves.hasNext()) {
            slaves.next().disconnect(world);
        }

        this.slaves.clear();
    }

    @Override
    public List<ItemStack> getItems() {
        return items;
    }

    @Override
    public List<ICraftingTask> getCraftingTasks() {
        return craftingTasks;
    }

    @Override
    public void addCraftingTask(ICraftingTask task) {
        craftingTasksToAdd.add(task);

        markDirty();
    }

    @Override
    public void addCraftingTaskAsLast(ICraftingTask task) {
        craftingTasksToAddAsLast.add(task);

        markDirty();
    }

    @Override
    public ICraftingTask createCraftingTask(ICraftingPattern pattern) {
        if (pattern.isProcessing()) {
            return new ProcessingCraftingTask(pattern);
        } else {
            return new BasicCraftingTask(pattern);
        }
    }

    @Override
    public void cancelCraftingTask(ICraftingTask task) {
        craftingTasksToCancel.add(task);

        markDirty();
    }

    @Override
    public List<ICraftingPattern> getPatterns() {
        return patterns;
    }

    @Override
    public List<ICraftingPattern> getPattern(ItemStack pattern, int flags) {
        List<ICraftingPattern> patterns = new ArrayList<ICraftingPattern>();

        for (ICraftingPattern craftingPattern : getPatterns()) {
            for (ItemStack output : craftingPattern.getOutputs()) {
                if (RefinedStorageUtils.compareStack(output, pattern, flags)) {
                    patterns.add(craftingPattern);
                }
            }
        }

        return patterns;
    }

    @Override
    public ICraftingPattern getPatternWithBestScore(ItemStack pattern) {
        return getPatternWithBestScore(pattern, CompareFlags.COMPARE_DAMAGE | CompareFlags.COMPARE_NBT);
    }

    @Override
    public ICraftingPattern getPatternWithBestScore(ItemStack pattern, int flags) {
        List<ICraftingPattern> patterns = getPattern(pattern, flags);

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

    private void updateSlaves() {
        this.energyUsage = 0;
        this.storages.clear();
        this.patterns.clear();

        int range = 0;

        Iterator<INetworkSlave> slaves = getSlaves();
        while (slaves.hasNext()) {
            INetworkSlave slave = slaves.next();

            if (!slave.canUpdate()) {
                continue;
            }

            if (slave instanceof TileWirelessTransmitter) {
                range += ((TileWirelessTransmitter) slave).getRange();
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

        wirelessGridHandler.setRange(range);

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

        updateItems();
        updateItemsWithClient();
    }

    private void updateItems() {
        items.clear();

        for (IStorage storage : storages) {
            storage.addItems(items);
        }

        for (ICraftingPattern pattern : patterns) {
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

    @Override
    public void updateItemsWithClient() {
        for (EntityPlayer player : world.playerEntities) {
            if (player.openContainer.getClass() == ContainerGrid.class && pos.equals(((ContainerGrid) player.openContainer).getGrid().getNetworkPosition())) {
                updateItemsWithClient((EntityPlayerMP) player);
            }
        }
    }

    @Override
    public void updateItemsWithClient(EntityPlayerMP player) {
        RefinedStorage.NETWORK.sendTo(new MessageGridItems(this), player);
    }

    @Override
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

        int sizePushed = remainder != null ? (orginalSize - remainder.stackSize) : orginalSize;

        if (!simulate && sizePushed > 0) {
            updateItems();
            updateItemsWithClient();

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

    @Override
    public ItemStack take(ItemStack stack, int size) {
        return take(stack, size, CompareFlags.COMPARE_DAMAGE | CompareFlags.COMPARE_NBT);
    }

    @Override
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
            updateItems();
            updateItemsWithClient();
        }

        return newStack;
    }

    @Override
    public ItemStack getItem(ItemStack stack, int flags) {
        for (ItemStack otherStack : items) {
            if (RefinedStorageUtils.compareStack(otherStack, stack, flags)) {
                return otherStack;
            }
        }

        return null;
    }

    @Override
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

    @Override
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
        if (world != null) {
            NetworkMasterSavedData.getOrLoad(world).markDirty();
        }
    }
}
