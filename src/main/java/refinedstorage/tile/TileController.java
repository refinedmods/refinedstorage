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
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;
import refinedstorage.RS;
import refinedstorage.RSBlocks;
import refinedstorage.RSUtils;
import refinedstorage.api.autocrafting.ICraftingPattern;
import refinedstorage.api.autocrafting.ICraftingPatternContainer;
import refinedstorage.api.autocrafting.ICraftingPatternProvider;
import refinedstorage.api.autocrafting.registry.ICraftingTaskFactory;
import refinedstorage.api.autocrafting.task.ICraftingTask;
import refinedstorage.api.autocrafting.task.IProcessable;
import refinedstorage.api.network.INetworkMaster;
import refinedstorage.api.network.INetworkNode;
import refinedstorage.api.network.INetworkNodeGraph;
import refinedstorage.api.network.IWirelessGridHandler;
import refinedstorage.api.network.grid.IFluidGridHandler;
import refinedstorage.api.network.grid.IItemGridHandler;
import refinedstorage.api.storage.fluid.IFluidStorage;
import refinedstorage.api.storage.fluid.IGroupedFluidStorage;
import refinedstorage.api.storage.item.IGroupedItemStorage;
import refinedstorage.api.storage.item.IItemStorage;
import refinedstorage.api.util.IComparer;
import refinedstorage.apiimpl.API;
import refinedstorage.apiimpl.network.NetworkNodeGraph;
import refinedstorage.apiimpl.network.WirelessGridHandler;
import refinedstorage.apiimpl.network.grid.FluidGridHandler;
import refinedstorage.apiimpl.network.grid.ItemGridHandler;
import refinedstorage.apiimpl.storage.fluid.GroupedFluidStorage;
import refinedstorage.apiimpl.storage.item.GroupedItemStorage;
import refinedstorage.block.BlockController;
import refinedstorage.block.EnumControllerType;
import refinedstorage.block.EnumGridType;
import refinedstorage.container.ContainerGrid;
import refinedstorage.integration.forgeenergy.ControllerEnergyForge;
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
import refinedstorage.tile.config.IAccessType;
import refinedstorage.tile.config.IRedstoneConfigurable;
import refinedstorage.tile.config.RedstoneMode;
import refinedstorage.tile.data.ITileDataProducer;
import refinedstorage.tile.data.RSSerializers;
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

    public static final TileDataParameter<List<ClientNode>> NODES = new TileDataParameter<>(RSSerializers.CLIENT_NODE_SERIALIZER, new ArrayList<>(), new ITileDataProducer<List<ClientNode>, TileController>() {
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

    private EnergyStorage energy = new EnergyStorage(RS.INSTANCE.config.controllerCapacity);
    private ControllerEnergyForge energyForge = new ControllerEnergyForge(this);
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
                    ICraftingTask task = readCraftingTask(worldObj, this, tag);

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
                    taskToCancel.onCancelled();
                }

                craftingTasks.removeAll(craftingTasksToCancel);
                craftingTasksToCancel.clear();

                for (ICraftingTask task : craftingTasksToAdd) {
                    craftingTasks.add(task);
                }

                craftingTasksToAdd.clear();

                Iterator<ICraftingTask> craftingTaskIterator = craftingTasks.iterator();

                while (craftingTaskIterator.hasNext()) {
                    ICraftingTask task = craftingTaskIterator.next();

                    ICraftingPatternContainer container = task.getPattern().getContainer();

                    if (container != null && ticks % container.getSpeed() == 0 && task.update()) {
                        craftingTaskIterator.remove();

                        craftingTasksChanged = true;
                    }
                }

                if (!craftingTasks.isEmpty() || craftingTasksChanged) {
                    markDirty();

                    updateCraftingMonitors();
                }
            }

            wirelessGridHandler.update();

            if (getType() == EnumControllerType.NORMAL) {
                if (!RS.INSTANCE.config.controllerUsesEnergy) {
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

                nodeGraph.rebuild();
            }

            if (getEnergyScaledForDisplay() != lastEnergyDisplay) {
                lastEnergyDisplay = getEnergyScaledForDisplay();

                updateBlock();
            }
        }

        super.update();
    }

    public void updateCraftingMonitors() {
        for (INetworkNode node : nodeGraph.all()) {
            if (node instanceof TileCraftingMonitor) {
                ((TileCraftingMonitor) node).dataManager.sendParameterToWatchers(TileCraftingMonitor.ELEMENTS);
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
                if (API.instance().getComparer().isEqual(output, pattern, flags)) {
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
                ItemStack stored = itemStorage.getList().get(input, IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT);

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
                patterns.addAll(((ICraftingPatternContainer) node).getPatterns());
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
        RS.INSTANCE.network.sendTo(new MessageGridItemUpdate(this), player);
    }

    @Override
    public void sendItemStorageDeltaToClient(ItemStack stack, int delta) {
        worldObj.getMinecraftServer().getPlayerList().getPlayerList().stream()
            .filter(player -> isWatchingGrid(player, EnumGridType.NORMAL, EnumGridType.CRAFTING, EnumGridType.PATTERN))
            .forEach(player -> RS.INSTANCE.network.sendTo(new MessageGridItemDelta(this, stack, delta), player));
    }

    @Override
    public void sendFluidStorageToClient() {
        worldObj.getMinecraftServer().getPlayerList().getPlayerList().stream()
            .filter(player -> isWatchingGrid(player, EnumGridType.FLUID))
            .forEach(this::sendFluidStorageToClient);
    }

    @Override
    public void sendFluidStorageToClient(EntityPlayerMP player) {
        RS.INSTANCE.network.sendTo(new MessageGridFluidUpdate(this), player);
    }

    @Override
    public void sendFluidStorageDeltaToClient(FluidStack stack, int delta) {
        worldObj.getMinecraftServer().getPlayerList().getPlayerList().stream()
            .filter(player -> isWatchingGrid(player, EnumGridType.FLUID))
            .forEach(player -> RS.INSTANCE.network.sendTo(new MessageGridFluidDelta(stack, delta), player));
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
        int accessType = IAccessType.READ_WRITE;
        ItemStack remainder = stack;

        for (IItemStorage storage : this.itemStorage.getStorages()) {
            accessType = storage.getAccessType();

            if (accessType != IAccessType.READ) {
                remainder = storage.insertItem(remainder, size, simulate);
            }


            if (storage instanceof ItemStorageExternal && !simulate) {
                ((ItemStorageExternal) storage).updateCacheForcefully();
            }

            if (remainder == null || remainder.stackSize < 0) {
                break;
            } else {
                size = remainder.stackSize;
            }
        }

        // If the stack size of the remainder is negative, it means of the original size abs(remainder.stackSize) items have been voided
        int inserted;

        if (remainder == null) {
            inserted = orginalSize;
        } else if (remainder.stackSize < 0) {
            inserted = orginalSize + remainder.stackSize;
            remainder = null;
        } else {
            inserted = orginalSize - remainder.stackSize;
        }

        if (!simulate && inserted > 0 && accessType != IAccessType.WRITE) {
            itemStorage.add(ItemHandlerHelper.copyStackWithSize(stack, inserted), false);

            for (int i = 0; i < inserted; ++i) {
                for (ICraftingTask task : craftingTasks) {
                    if (inserted == 0) {
                        break;
                    }

                    for (IProcessable processable : task.getToProcess()) {
                        if (inserted == 0) {
                            break;
                        }

                        if (processable.onReceiveOutput(stack)) {
                            inserted--;
                        }
                    }
                }
            }
        }

        return remainder;
    }

    @Override
    public ItemStack extractItem(ItemStack stack, int size, int flags) {
        int requested = size;
        int received = 0;
        ItemStack newStack = null;

        for (IItemStorage storage : this.itemStorage.getStorages()) {
            ItemStack took = null;

            if (storage.getAccessType() != IAccessType.READ) {
                took = storage.extractItem(stack, requested - received, flags);
            }

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
            return RSUtils.copyStackWithSize(stack, size);
        }

        int orginalSize = size;
        int accessType = IAccessType.READ_WRITE;
        FluidStack remainder = stack;

        for (IFluidStorage storage : this.fluidStorage.getStorages()) {
            accessType = storage.getAccessType();

            if (accessType != IAccessType.READ) {
                remainder = storage.insertFluid(remainder, size, simulate);
            }

            if (storage instanceof FluidStorageExternal && !simulate) {
                ((FluidStorageExternal) storage).updateCacheForcefully();
            }

            if (remainder == null) {
                break;
            } else {
                size = remainder.amount;
            }
        }

        // If the stack size of the remainder is negative, it means of the original size abs(remainder.amount) fluids have been voided
        int inserted;

        if (remainder == null) {
            inserted = orginalSize;
        } else if (remainder.amount < 0) {
            inserted = orginalSize + remainder.amount;
            remainder = null;
        } else {
            inserted = orginalSize - remainder.amount;
        }

        if (!simulate && inserted > 0 && accessType != IAccessType.WRITE) {
            fluidStorage.add(RSUtils.copyStackWithSize(stack, inserted), false);
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
            FluidStack took = null;
            if (storage.getAccessType() != IAccessType.READ) {
                took = storage.extractFluid(stack, requested - received, flags);
            }

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

    public static ICraftingTask readCraftingTask(World world, INetworkMaster network, NBTTagCompound tag) {
        ItemStack stack = ItemStack.loadItemStackFromNBT(tag.getCompoundTag(ICraftingTask.NBT_PATTERN_STACK));

        if (stack != null && stack.getItem() instanceof ICraftingPatternProvider) {
            TileEntity container = world.getTileEntity(BlockPos.fromLong(tag.getLong(ICraftingTask.NBT_PATTERN_CONTAINER)));

            if (container instanceof ICraftingPatternContainer) {
                ICraftingPattern pattern = ((ICraftingPatternProvider) stack.getItem()).create(world, stack, (ICraftingPatternContainer) container);

                ICraftingTaskFactory factory = API.instance().getCraftingTaskRegistry().getFactory(tag.getString(ICraftingTask.NBT_PATTERN_ID));

                if (factory != null) {
                    return factory.create(world, network, null, pattern, tag.getInteger(ICraftingTask.NBT_QUANTITY), tag);
                }
            }
        }

        return null;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);

        energy.readFromNBT(tag);

        redstoneMode = RedstoneMode.read(tag);

        if (tag.hasKey(NBT_CRAFTING_TASKS)) {
            NBTTagList taskList = tag.getTagList(NBT_CRAFTING_TASKS, Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < taskList.tagCount(); ++i) {
                craftingTasksToRead.add(taskList.getCompoundTagAt(i));
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);

        energy.writeToNBT(tag);

        redstoneMode.write(tag);

        NBTTagList list = new NBTTagList();

        for (ICraftingTask task : craftingTasks) {
            list.appendTag(task.writeToNBT(new NBTTagCompound()));
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
        int usage = RS.INSTANCE.config.controllerBaseUsage;

        for (INetworkNode node : nodeGraph.all()) {
            if (node.canUpdate()) {
                usage += node.getEnergyUsage();
            }
        }

        return usage;
    }

    public EnumControllerType getType() {
        if (type == null && worldObj.getBlockState(pos).getBlock() == RSBlocks.CONTROLLER) {
            this.type = (EnumControllerType) worldObj.getBlockState(pos).getValue(BlockController.TYPE);
        }

        return type == null ? EnumControllerType.NORMAL : type;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return (T) energyForge;
        }

        if (energyTesla != null && (capability == TeslaCapabilities.CAPABILITY_HOLDER || capability == TeslaCapabilities.CAPABILITY_CONSUMER)) {
            return (T) energyTesla;
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY
            || (energyTesla != null && (capability == TeslaCapabilities.CAPABILITY_HOLDER || capability == TeslaCapabilities.CAPABILITY_CONSUMER))
            || super.hasCapability(capability, facing);
    }
}
