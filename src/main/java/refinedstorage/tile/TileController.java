package refinedstorage.tile;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.RefinedStorage;
import refinedstorage.RefinedStorageBlocks;
import refinedstorage.api.RefinedStorageAPI;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.ICraftingPatternContainer;
import refinedstorage.api.autocrafting.ICraftingPatternProvider;
import refinedstorage.api.autocrafting.registry.ICraftingTaskFactory;
import refinedstorage.api.autocrafting.task.ICraftingTask;
import refinedstorage.api.network.*;
import refinedstorage.api.network.grid.IFluidGridHandler;
import refinedstorage.api.network.grid.IItemGridHandler;
import refinedstorage.api.storage.CompareUtils;
import refinedstorage.api.storage.fluid.IFluidStorage;
import refinedstorage.api.storage.fluid.IGroupedFluidStorage;
import refinedstorage.api.storage.item.IGroupedItemStorage;
import refinedstorage.api.storage.item.IItemStorage;
import refinedstorage.apiimpl.autocrafting.task.CraftingTaskProcessing;
import refinedstorage.apiimpl.network.NetworkNodeGraph;
import refinedstorage.apiimpl.network.WirelessGridHandler;
import refinedstorage.apiimpl.network.grid.FluidGridHandler;
import refinedstorage.apiimpl.network.grid.ItemGridHandler;
import refinedstorage.apiimpl.storage.fluid.FluidUtils;
import refinedstorage.apiimpl.storage.fluid.GroupedFluidStorage;
import refinedstorage.apiimpl.storage.item.GroupedItemStorage;
import refinedstorage.block.BlockController;
import refinedstorage.block.EnumControllerType;
import refinedstorage.block.EnumGridType;
import refinedstorage.container.ContainerGrid;
import refinedstorage.integration.ic2.ControllerEnergyIC2;
import refinedstorage.integration.ic2.ControllerEnergyIC2None;
import refinedstorage.integration.ic2.IControllerEnergyIC2;
import refinedstorage.integration.ic2.IntegrationIC2;
import refinedstorage.integration.tesla.ControllerEnergyTesla;
import refinedstorage.integration.tesla.IntegrationTesla;
import refinedstorage.network.MessageGridFluidDelta;
import refinedstorage.network.MessageGridFluidUpdate;
import refinedstorage.network.MessageGridItemDelta;
import refinedstorage.network.MessageGridItemUpdate;
import refinedstorage.tile.config.IRedstoneConfigurable;
import refinedstorage.tile.config.RedstoneMode;
import refinedstorage.tile.data.ITileDataProducer;
import refinedstorage.tile.data.RefinedStorageSerializers;
import refinedstorage.tile.data.TileDataParameter;
import refinedstorage.tile.externalstorage.FluidStorageExternal;
import refinedstorage.tile.externalstorage.ItemStorageExternal;
import refinedstorage.tile.grid.IGrid;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class TileController extends TileBase implements INetworkMaster, IEnergyReceiver, IRedstoneConfigurable {
    public static final TileDataParameter<Integer> REDSTONE_MODE = RedstoneMode.createParameter();

    public static final TileDataParameter<Integer> ENERGY_USAGE = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileController>() {
        @Override
        public Integer getValue(TileController tile) {
            return tile.getEnergyUsage();
        }
    });

    public static final TileDataParameter<Integer> ENERGY_STORED = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileController>() {
        @Override
        public Integer getValue(TileController tile) {
            return tile.getEnergy().getEnergyStored();
        }
    });

    public static final TileDataParameter<Integer> ENERGY_CAPACITY = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileController>() {
        @Override
        public Integer getValue(TileController tile) {
            return tile.getEnergy().getMaxEnergyStored();
        }
    });

    public static final TileDataParameter<List<ClientNode>> NODES = new TileDataParameter<>(RefinedStorageSerializers.CLIENT_NODE_SERIALIZER, new ArrayList<>(), new ITileDataProducer<List<ClientNode>, TileController>() {
        @Override
        public List<ClientNode> getValue(TileController tile) {
            List<ClientNode> nodes = new ArrayList<>();

            for (INetworkNode node : tile.nodeGraph.all()) {
                if (node.canUpdate()) {
                    IBlockState state = tile.worldObj.getBlockState(node.getPosition());

                    ClientNode clientNode = new ClientNode(
                        new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state)),
                        1,
                        node.getEnergyUsage()
                    );

                    if (clientNode.getStack().getItem() != null) {
                        if (nodes.contains(clientNode)) {
                            for (ClientNode other : nodes) {
                                if (other.equals(clientNode)) {
                                    other.setAmount(other.getAmount() + 1);

                                    break;
                                }
                            }
                        } else {
                            nodes.add(clientNode);
                        }
                    }
                }
            }

            return nodes;
        }
    });

    public static final String NBT_ENERGY = "Energy";
    public static final String NBT_ENERGY_CAPACITY = "EnergyCapacity";

    private static final String NBT_CRAFTING_TASKS = "CraftingTasks";
    private static final String NBT_CRAFTING_TASK_PATTERN = "Pattern";
    private static final String NBT_CRAFTING_TASK_TYPE = "Type";
    private static final String NBT_CRAFTING_TASK_CONTAINER = "Container";

    private static final Comparator<IItemStorage> ITEM_SIZE_COMPARATOR = (left, right) -> {
        if (left.getStored() == right.getStored()) {
            return 0;
        }

        return (left.getStored() > right.getStored()) ? -1 : 1;
    };

    private static final Comparator<IItemStorage> ITEM_PRIORITY_COMPARATOR = (left, right) -> {
        if (left.getPriority() == right.getPriority()) {
            return 0;
        }

        return (left.getPriority() > right.getPriority()) ? -1 : 1;
    };

    private static final Comparator<IFluidStorage> FLUID_SIZE_COMPARATOR = (left, right) -> {
        if (left.getStored() == right.getStored()) {
            return 0;
        }

        return (left.getStored() > right.getStored()) ? -1 : 1;
    };

    private static final Comparator<IFluidStorage> FLUID_PRIORITY_COMPARATOR = (left, right) -> {
        if (left.getPriority() == right.getPriority()) {
            return 0;
        }

        return (left.getPriority() > right.getPriority()) ? -1 : 1;
    };

    private IItemGridHandler itemGridHandler = new ItemGridHandler(this);
    private IFluidGridHandler fluidGridHandler = new FluidGridHandler(this);

    private IWirelessGridHandler wirelessGridHandler = new WirelessGridHandler(this);

    private INetworkNodeGraph nodeGraph = new NetworkNodeGraph(this);

    private IGroupedItemStorage itemStorage = new GroupedItemStorage(this);
    private IGroupedFluidStorage fluidStorage = new GroupedFluidStorage(this);

    private List<ICraftingPattern> patterns = new ArrayList<>();

    private List<ICraftingTask> craftingTasks = new ArrayList<>();
    private List<ICraftingTask> craftingTasksToAdd = new ArrayList<>();
    private List<ICraftingTask> craftingTasksToCancel = new ArrayList<>();
    private List<NBTTagCompound> craftingTasksToRead = new ArrayList<>();

    private EnergyStorage energy = new EnergyStorage(RefinedStorage.INSTANCE.controllerCapacity);
    private IControllerEnergyIC2 energyEU;
    private ControllerEnergyTesla energyTesla;

    private int lastEnergyDisplay;

    private boolean couldRun;

    private EnumControllerType type;

    private RedstoneMode redstoneMode = RedstoneMode.IGNORE;

    public TileController() {
        dataManager.addWatchedParameter(REDSTONE_MODE);
        dataManager.addWatchedParameter(ENERGY_USAGE);
        dataManager.addWatchedParameter(ENERGY_STORED);
        dataManager.addParameter(ENERGY_CAPACITY);
        dataManager.addParameter(NODES);

        if (IntegrationIC2.isLoaded()) {
            this.energyEU = new ControllerEnergyIC2(this);
        } else {
            this.energyEU = new ControllerEnergyIC2None();
        }

        if (IntegrationTesla.isLoaded()) {
            this.energyTesla = new ControllerEnergyTesla(energy);
        }
    }

    @Override
    public BlockPos getPosition() {
        return pos;
    }

    @Override
    public EnergyStorage getEnergy() {
        return energy;
    }

    @Override
    public boolean canRun() {
        return energy.getEnergyStored() > 0 && redstoneMode.isEnabled(worldObj, pos);
    }

    @Override
    public INetworkNodeGraph getNodeGraph() {
        return nodeGraph;
    }

    @Override
    public void update() {
        if (!worldObj.isRemote) {
            energyEU.update();

            if (!craftingTasksToRead.isEmpty()) {
                for (NBTTagCompound tag : craftingTasksToRead) {
                    ICraftingTask task = readCraftingTask(worldObj, tag);

                    if (task != null) {
                        addCraftingTask(task);
                    }
                }

                craftingTasksToRead.clear();
            }

            if (canRun()) {
                Collections.sort(itemStorage.getStorages(), ITEM_SIZE_COMPARATOR);
                Collections.sort(itemStorage.getStorages(), ITEM_PRIORITY_COMPARATOR);

                Collections.sort(fluidStorage.getStorages(), FLUID_SIZE_COMPARATOR);
                Collections.sort(fluidStorage.getStorages(), FLUID_PRIORITY_COMPARATOR);

                boolean craftingTasksChanged = !craftingTasksToAdd.isEmpty() || !craftingTasksToCancel.isEmpty();

                for (ICraftingTask taskToCancel : craftingTasksToCancel) {
                    taskToCancel.onCancelled(this);
                }

                craftingTasks.removeAll(craftingTasksToCancel);
                craftingTasksToCancel.clear();

                for (ICraftingTask task : craftingTasksToAdd) {
                    craftingTasks.add(task);
                }

                craftingTasksToAdd.clear();

                if (!craftingTasks.isEmpty()) {
                    markDirty();
                }

                Iterator<ICraftingTask> craftingTaskIterator = craftingTasks.iterator();

                while (craftingTaskIterator.hasNext()) {
                    ICraftingTask task = craftingTaskIterator.next();

                    if (updateCraftingTask(task)) {
                        craftingTaskIterator.remove();

                        craftingTasksChanged = true;
                    }
                }

                if (craftingTasksChanged) {
                    updateCraftingTasks();
                }
            }

            wirelessGridHandler.update();

            if (getType() == EnumControllerType.NORMAL) {
                if (!RefinedStorage.INSTANCE.controllerUsesEnergy) {
                    energy.setEnergyStored(energy.getMaxEnergyStored());
                } else if (energy.getEnergyStored() - getEnergyUsage() >= 0) {
                    energy.extractEnergy(getEnergyUsage(), false);
                } else {
                    energy.setEnergyStored(0);
                }
            } else if (getType() == EnumControllerType.CREATIVE) {
                energy.setEnergyStored(energy.getMaxEnergyStored());
            }

            if (couldRun != canRun()) {
                couldRun = canRun();

                NetworkUtils.rebuildGraph(this);
            }

            if (getEnergyScaledForDisplay() != lastEnergyDisplay) {
                lastEnergyDisplay = getEnergyScaledForDisplay();

                updateBlock();
            }
        }

        super.update();
    }

    private boolean updateCraftingTask(ICraftingTask task) {
        if (task.getChild() != null) {
            if (updateCraftingTask(task.getChild())) {
                task.setChild(null);
            }

            return false;
        }

        ICraftingPatternContainer container = task.getPattern().getContainer();

        return container != null && ticks % container.getSpeed() == 0 && task.update(worldObj, this);
    }

    @Override
    public void updateCraftingTasks() {
        markDirty();

        for (INetworkNode node : nodeGraph.all()) {
            if (node instanceof TileCraftingMonitor) {
                ((TileCraftingMonitor) node).dataManager.sendParameterToWatchers(TileCraftingMonitor.TASKS);
            }
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();

        energyEU.invalidate();
    }

    @Override
    public IItemGridHandler getItemGridHandler() {
        return itemGridHandler;
    }

    @Override
    public IFluidGridHandler getFluidGridHandler() {
        return fluidGridHandler;
    }

    @Override
    public IWirelessGridHandler getWirelessGridHandler() {
        return wirelessGridHandler;
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();

        energyEU.onChunkUnload();
    }

    public void onDestroyed() {
        nodeGraph.disconnectAll();
    }

    public IGroupedItemStorage getItemStorage() {
        return itemStorage;
    }

    @Override
    public IGroupedFluidStorage getFluidStorage() {
        return fluidStorage;
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
    public void cancelCraftingTask(ICraftingTask task) {
        craftingTasksToCancel.add(task);

        markDirty();
    }

    @Override
    public List<ICraftingPattern> getPatterns() {
        return patterns;
    }

    @Override
    public List<ICraftingPattern> getPatterns(ItemStack pattern, int flags) {
        List<ICraftingPattern> patterns = new ArrayList<>();

        for (ICraftingPattern craftingPattern : getPatterns()) {
            for (ItemStack output : craftingPattern.getOutputs()) {
                if (CompareUtils.compareStack(output, pattern, flags)) {
                    patterns.add(craftingPattern);
                }
            }
        }

        return patterns;
    }

    @Override
    public ICraftingPattern getPattern(ItemStack pattern, int flags) {
        List<ICraftingPattern> patterns = getPatterns(pattern, flags);

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
                ItemStack stored = itemStorage.get(input, CompareUtils.COMPARE_DAMAGE | CompareUtils.COMPARE_NBT);

                score += stored != null ? stored.stackSize : 0;
            }

            if (score > highestScore) {
                highestScore = score;
                highestPattern = i;
            }
        }

        return patterns.get(highestPattern);
    }

    @Override
    public void rebuildPatterns() {
        patterns.clear();

        for (INetworkNode node : nodeGraph.all()) {
            if (node instanceof ICraftingPatternContainer && node.canUpdate()) {
                ICraftingPatternContainer container = (ICraftingPatternContainer) node;

                for (int i = 0; i < container.getPatterns().getSlots(); ++i) {
                    ItemStack stack = container.getPatterns().getStackInSlot(i);

                    if (stack != null) {
                        ICraftingPattern pattern = ((ICraftingPatternProvider) stack.getItem()).create(worldObj, stack, container);

                        if (pattern.isValid()) {
                            patterns.add(pattern);
                        }
                    }
                }
            }
        }

        itemStorage.rebuild();
    }

    @Override
    public void sendItemStorageToClient() {
        worldObj.getMinecraftServer().getPlayerList().getPlayerList().stream()
            .filter(player -> isWatchingGrid(player, EnumGridType.NORMAL, EnumGridType.CRAFTING, EnumGridType.PATTERN))
            .forEach(this::sendItemStorageToClient);
    }

    @Override
    public void sendItemStorageToClient(EntityPlayerMP player) {
        RefinedStorage.INSTANCE.network.sendTo(new MessageGridItemUpdate(this), player);
    }

    @Override
    public void sendItemStorageDeltaToClient(ItemStack stack, int delta) {
        worldObj.getMinecraftServer().getPlayerList().getPlayerList().stream()
            .filter(player -> isWatchingGrid(player, EnumGridType.NORMAL, EnumGridType.CRAFTING, EnumGridType.PATTERN))
            .forEach(player -> RefinedStorage.INSTANCE.network.sendTo(new MessageGridItemDelta(this, stack, delta), player));
    }

    @Override
    public void sendFluidStorageToClient() {
        worldObj.getMinecraftServer().getPlayerList().getPlayerList().stream()
            .filter(player -> isWatchingGrid(player, EnumGridType.FLUID))
            .forEach(this::sendFluidStorageToClient);
    }

    @Override
    public void sendFluidStorageToClient(EntityPlayerMP player) {
        RefinedStorage.INSTANCE.network.sendTo(new MessageGridFluidUpdate(this), player);
    }

    @Override
    public void sendFluidStorageDeltaToClient(FluidStack stack, int delta) {
        worldObj.getMinecraftServer().getPlayerList().getPlayerList().stream()
            .filter(player -> isWatchingGrid(player, EnumGridType.FLUID))
            .forEach(player -> RefinedStorage.INSTANCE.network.sendTo(new MessageGridFluidDelta(stack, delta), player));
    }

    private boolean isWatchingGrid(EntityPlayer player, EnumGridType... types) {
        if (player.openContainer.getClass() == ContainerGrid.class) {
            IGrid grid = ((ContainerGrid) player.openContainer).getGrid();

            if (pos.equals(grid.getNetworkPosition())) {
                return Arrays.asList(types).contains(grid.getType());
            }
        }

        return false;
    }

    @Override
    public ItemStack insertItem(ItemStack stack, int size, boolean simulate) {
        if (stack == null || stack.getItem() == null || itemStorage.getStorages().isEmpty()) {
            return ItemHandlerHelper.copyStackWithSize(stack, size);
        }

        int orginalSize = size;

        ItemStack remainder = stack;

        for (IItemStorage storage : this.itemStorage.getStorages()) {
            remainder = storage.insertItem(remainder, size, simulate);

            if (storage instanceof ItemStorageExternal && !simulate) {
                ((ItemStorageExternal) storage).updateCacheForcefully();
            }

            if (remainder == null) {
                break;
            } else {
                size = remainder.stackSize;
            }
        }

        int inserted = remainder != null ? (orginalSize - remainder.stackSize) : orginalSize;

        if (!simulate && inserted > 0) {
            itemStorage.add(ItemHandlerHelper.copyStackWithSize(stack, inserted), false);

            for (int i = 0; i < inserted; ++i) {
                for (ICraftingTask task : craftingTasks) {
                    if (inserted == 0) {
                        break;
                    }

                    if (checkProcessing(stack, task)) {
                        inserted--;
                    }
                }
            }
        }

        return remainder;
    }

    private boolean checkProcessing(ItemStack stack, ICraftingTask task) {
        if (task.getChild() instanceof CraftingTaskProcessing) {
            if (checkProcessing(stack, task.getChild())) {
                return true;
            }
        }

        return task instanceof CraftingTaskProcessing && ((CraftingTaskProcessing) task).onInserted(stack);
    }

    @Override
    public ItemStack extractItem(ItemStack stack, int size, int flags) {
        int requested = size;
        int received = 0;

        ItemStack newStack = null;

        for (IItemStorage storage : this.itemStorage.getStorages()) {
            ItemStack took = storage.extractItem(stack, requested - received, flags);

            if (took != null) {
                if (storage instanceof ItemStorageExternal) {
                    ((ItemStorageExternal) storage).updateCacheForcefully();
                }

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
            itemStorage.remove(newStack);
        }

        return newStack;
    }

    @Nullable
    @Override
    public FluidStack insertFluid(@Nonnull FluidStack stack, int size, boolean simulate) {
        if (stack == null || fluidStorage.getStorages().isEmpty()) {
            return FluidUtils.copyStackWithSize(stack, size);
        }

        int orginalSize = size;

        FluidStack remainder = stack;

        for (IFluidStorage storage : this.fluidStorage.getStorages()) {
            remainder = storage.insertFluid(remainder, size, simulate);

            if (storage instanceof FluidStorageExternal && !simulate) {
                ((FluidStorageExternal) storage).updateCacheForcefully();
            }

            if (remainder == null) {
                break;
            } else {
                size = remainder.amount;
            }
        }

        int inserted = remainder != null ? (orginalSize - remainder.amount) : orginalSize;

        if (!simulate && inserted > 0) {
            fluidStorage.add(FluidUtils.copyStackWithSize(stack, inserted), false);
        }

        return remainder;
    }

    @Nullable
    @Override
    public FluidStack extractFluid(@Nonnull FluidStack stack, int size, int flags) {
        int requested = size;
        int received = 0;

        FluidStack newStack = null;

        for (IFluidStorage storage : this.fluidStorage.getStorages()) {
            FluidStack took = storage.extractFluid(stack, requested - received, flags);

            if (took != null) {
                if (storage instanceof FluidStorageExternal) {
                    ((FluidStorageExternal) storage).updateCacheForcefully();
                }

                if (newStack == null) {
                    newStack = took;
                } else {
                    newStack.amount += took.amount;
                }

                received += took.amount;
            }

            if (requested == received) {
                break;
            }
        }

        if (newStack != null) {
            fluidStorage.remove(newStack);
        }

        return newStack;
    }

    @Override
    public World getNetworkWorld() {
        return worldObj;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);

        energy.readFromNBT(tag);

        if (tag.hasKey(RedstoneMode.NBT)) {
            redstoneMode = RedstoneMode.getById(tag.getInteger(RedstoneMode.NBT));
        }

        if (tag.hasKey(NBT_CRAFTING_TASKS)) {
            NBTTagList taskList = tag.getTagList(NBT_CRAFTING_TASKS, Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < taskList.tagCount(); ++i) {
                craftingTasksToRead.add(taskList.getCompoundTagAt(i));
            }
        }
    }

    public static ICraftingTask readCraftingTask(World world, NBTTagCompound tag) {
        ItemStack stack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag(NBT_CRAFTING_TASK_PATTERN));

        if (stack != null && stack.getItem() instanceof ICraftingPatternProvider) {
            TileEntity container = world.getTileEntity(BlockPos.fromLong(tag.getLong(NBT_CRAFTING_TASK_CONTAINER)));

            if (container instanceof ICraftingPatternContainer) {
                ICraftingPattern pattern = ((ICraftingPatternProvider) stack.getItem()).create(world, stack, (ICraftingPatternContainer) container);

                ICraftingTaskFactory factory = RefinedStorageAPI.instance().getCraftingTaskRegistry().getFactory(tag.getString(NBT_CRAFTING_TASK_TYPE));

                if (factory != null) {
                    return factory.create(world, tag, pattern);
                }
            }
        }

        return null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);

        energy.writeToNBT(tag);

        tag.setInteger(RedstoneMode.NBT, redstoneMode.ordinal());

        NBTTagList list = new NBTTagList();

        for (ICraftingTask task : craftingTasks) {
            NBTTagCompound taskTag = new NBTTagCompound();

            task.writeToNBT(taskTag);

            taskTag.setString(NBT_CRAFTING_TASK_TYPE, task.getPattern().getId());
            taskTag.setTag(NBT_CRAFTING_TASK_PATTERN, task.getPattern().getStack().serializeNBT());
            taskTag.setLong(NBT_CRAFTING_TASK_CONTAINER, task.getPattern().getContainer().getPosition().toLong());

            list.appendTag(taskTag);
        }

        tag.setTag(NBT_CRAFTING_TASKS, list);

        return tag;
    }

    @Override
    public NBTTagCompound writeUpdate(NBTTagCompound tag) {
        super.writeUpdate(tag);

        tag.setInteger(NBT_ENERGY_CAPACITY, energy.getMaxEnergyStored());
        tag.setInteger(NBT_ENERGY, energy.getEnergyStored());

        return tag;
    }

    @Override
    public void readUpdate(NBTTagCompound tag) {
        energy.setCapacity(tag.getInteger(NBT_ENERGY_CAPACITY));
        energy.setEnergyStored(tag.getInteger(NBT_ENERGY));

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

    public static int getEnergyScaled(int stored, int capacity, int scale) {
        return (int) ((float) stored / (float) capacity * (float) scale);
    }

    public int getEnergyScaledForDisplay() {
        return getEnergyScaled(energy.getEnergyStored(), energy.getMaxEnergyStored(), 7);
    }

    @Override
    public int getMaxEnergyStored(EnumFacing from) {
        return energy.getMaxEnergyStored();
    }

    @Override
    public boolean canConnectEnergy(EnumFacing from) {
        return true;
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
    public int getEnergyUsage() {
        int usage = RefinedStorage.INSTANCE.controllerBaseUsage;

        for (INetworkNode node : nodeGraph.all()) {
            if (node.canUpdate()) {
                usage += node.getEnergyUsage();
            }
        }

        return usage;
    }

    public EnumControllerType getType() {
        if (type == null && worldObj.getBlockState(pos).getBlock() == RefinedStorageBlocks.CONTROLLER) {
            this.type = (EnumControllerType) worldObj.getBlockState(pos).getValue(BlockController.TYPE);
        }

        return type == null ? EnumControllerType.NORMAL : type;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (energyTesla != null && (capability == TeslaCapabilities.CAPABILITY_HOLDER || capability == TeslaCapabilities.CAPABILITY_CONSUMER)) {
            return (T) energyTesla;
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return (energyTesla != null && (capability == TeslaCapabilities.CAPABILITY_HOLDER || capability == TeslaCapabilities.CAPABILITY_CONSUMER)) || super.hasCapability(capability, facing);
    }
}
