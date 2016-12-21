package com.raoulvdberge.refinedstorage.tile;

import com.google.common.base.Preconditions;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPattern;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternProvider;
import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.raoulvdberge.refinedstorage.api.autocrafting.registry.ICraftingTaskFactory;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingStep;
import com.raoulvdberge.refinedstorage.api.autocrafting.task.ICraftingTask;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.api.network.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.INetworkNodeGraph;
import com.raoulvdberge.refinedstorage.api.network.INetworkNodeProxy;
import com.raoulvdberge.refinedstorage.api.network.grid.IFluidGridHandler;
import com.raoulvdberge.refinedstorage.api.network.grid.IItemGridHandler;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemHandler;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterChannel;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterHandler;
import com.raoulvdberge.refinedstorage.api.network.security.ISecurityManager;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCache;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.api.util.IStackList;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.NetworkNodeGraph;
import com.raoulvdberge.refinedstorage.apiimpl.network.grid.FluidGridHandler;
import com.raoulvdberge.refinedstorage.apiimpl.network.grid.ItemGridHandler;
import com.raoulvdberge.refinedstorage.apiimpl.network.item.NetworkItemHandler;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.externalstorage.StorageFluidExternal;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.externalstorage.StorageItemExternal;
import com.raoulvdberge.refinedstorage.apiimpl.network.security.SecurityManager;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageCacheFluid;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageCacheItem;
import com.raoulvdberge.refinedstorage.block.BlockController;
import com.raoulvdberge.refinedstorage.block.EnumControllerType;
import com.raoulvdberge.refinedstorage.block.EnumGridType;
import com.raoulvdberge.refinedstorage.container.ContainerCraftingMonitor;
import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import com.raoulvdberge.refinedstorage.container.ContainerReaderWriter;
import com.raoulvdberge.refinedstorage.integration.forgeenergy.ControllerEnergyForge;
import com.raoulvdberge.refinedstorage.integration.tesla.ControllerEnergyTesla;
import com.raoulvdberge.refinedstorage.integration.tesla.IntegrationTesla;
import com.raoulvdberge.refinedstorage.network.*;
import com.raoulvdberge.refinedstorage.proxy.CapabilityNetworkNodeProxy;
import com.raoulvdberge.refinedstorage.tile.config.IRedstoneConfigurable;
import com.raoulvdberge.refinedstorage.tile.config.RedstoneMode;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataProducer;
import com.raoulvdberge.refinedstorage.tile.data.RSSerializers;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import com.raoulvdberge.refinedstorage.tile.grid.IGrid;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class TileController extends TileBase implements INetworkMaster, IRedstoneConfigurable, INetworkNode, INetworkNodeProxy {
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
            return tile.energy.getEnergyStored();
        }
    });

    public static final TileDataParameter<Integer> ENERGY_CAPACITY = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileController>() {
        @Override
        public Integer getValue(TileController tile) {
            return tile.energy.getMaxEnergyStored();
        }
    });

    public static final TileDataParameter<List<ClientNode>> NODES = new TileDataParameter<>(RSSerializers.CLIENT_NODE_SERIALIZER, new ArrayList<>(), new ITileDataProducer<List<ClientNode>, TileController>() {
        @Override
        public List<ClientNode> getValue(TileController tile) {
            List<ClientNode> nodes = new ArrayList<>();

            for (INetworkNode node : tile.nodeGraph.all()) {
                if (node.canUpdate()) {
                    ItemStack stack = node.getItemStack();

                    if (stack.isEmpty()) {
                        continue;
                    }

                    ClientNode clientNode = new ClientNode(
                            stack,
                            1,
                            node.getEnergyUsage()
                    );

                    if (nodes.contains(clientNode)) {
                        ClientNode other = nodes.get(nodes.indexOf(clientNode));

                        other.setAmount(other.getAmount() + 1);
                    } else {
                        nodes.add(clientNode);
                    }
                }
            }

            Collections.sort(nodes, CLIENT_NODE_COMPARATOR);

            return nodes;
        }
    });

    public static final String NBT_ENERGY = "Energy";
    public static final String NBT_ENERGY_CAPACITY = "EnergyCapacity";

    private static final String NBT_CRAFTING_TASKS = "CraftingTasks";

    private static final String NBT_READER_WRITER_CHANNELS = "ReaderWriterChannels";
    private static final String NBT_READER_WRITER_NAME = "Name";

    private static final Comparator<ClientNode> CLIENT_NODE_COMPARATOR = (left, right) -> {
        if (left.getEnergyUsage() == right.getEnergyUsage()) {
            return 0;
        }

        return (left.getEnergyUsage() > right.getEnergyUsage()) ? -1 : 1;
    };

    private static final Comparator<IStorage> STORAGE_COMPARATOR = (left, right) -> {
        int compare = Integer.compare(right.getPriority(), left.getPriority());

        return compare != 0 ? compare : Integer.compare(right.getStored(), left.getStored());
    };

    private IItemGridHandler itemGridHandler = new ItemGridHandler(this);
    private IFluidGridHandler fluidGridHandler = new FluidGridHandler(this);

    private INetworkItemHandler networkItemHandler = new NetworkItemHandler(this);

    private INetworkNodeGraph nodeGraph = new NetworkNodeGraph(this);

    private ISecurityManager securityManager = new SecurityManager(this);

    private IStorageCache<ItemStack> itemStorage = new StorageCacheItem(this);
    private IStorageCache<FluidStack> fluidStorage = new StorageCacheFluid(this);

    private Map<String, IReaderWriterChannel> readerWriterChannels = new HashMap<>();

    private List<ICraftingPattern> patterns = new ArrayList<>();

    private List<ICraftingTask> craftingTasks = new ArrayList<>();
    private List<ICraftingTask> craftingTasksToAdd = new ArrayList<>();
    private List<ICraftingTask> craftingTasksToCancel = new ArrayList<>();
    private List<NBTTagCompound> craftingTasksToRead = new ArrayList<>();

    private ControllerEnergyForge energy = new ControllerEnergyForge();
    private ControllerEnergyTesla energyTesla;

    private int lastEnergyDisplay;

    private boolean couldRun;

    private boolean craftingMonitorUpdateRequested;

    private EnumControllerType type;

    private RedstoneMode redstoneMode = RedstoneMode.IGNORE;

    public TileController() {
        dataManager.addWatchedParameter(REDSTONE_MODE);
        dataManager.addWatchedParameter(ENERGY_USAGE);
        dataManager.addWatchedParameter(ENERGY_STORED);
        dataManager.addParameter(ENERGY_CAPACITY);
        dataManager.addParameter(NODES);

        if (IntegrationTesla.isLoaded()) {
            this.energyTesla = new ControllerEnergyTesla(energy);
        }
    }

    public ControllerEnergyForge getEnergy() {
        return energy;
    }

    @Override
    public BlockPos getPosition() {
        return pos;
    }

    @Override
    public boolean canRun() {
        return energy.getEnergyStored() > 0 && redstoneMode.isEnabled(getWorld(), pos);
    }

    @Override
    public INetworkNodeGraph getNodeGraph() {
        return nodeGraph;
    }

    @Override
    public ISecurityManager getSecurityManager() {
        return securityManager;
    }

    @Override
    public void update() {
        if (!getWorld().isRemote) {
            if (!craftingTasksToRead.isEmpty()) {
                for (NBTTagCompound tag : craftingTasksToRead) {
                    ICraftingTask task = readCraftingTask(getWorld(), this, tag);

                    if (task != null) {
                        addCraftingTask(task);
                    }
                }

                craftingTasksToRead.clear();
            }

            if (canRun()) {
                Collections.sort(itemStorage.getStorages(), STORAGE_COMPARATOR);
                Collections.sort(fluidStorage.getStorages(), STORAGE_COMPARATOR);

                boolean craftingTasksChanged = !craftingTasksToAdd.isEmpty() || !craftingTasksToCancel.isEmpty();

                craftingTasksToCancel.forEach(ICraftingTask::onCancelled);
                craftingTasks.removeAll(craftingTasksToCancel);
                craftingTasksToCancel.clear();

                craftingTasksToAdd.stream().filter(ICraftingTask::isValid).forEach(craftingTasks::add);
                craftingTasksToAdd.clear();

                // Only run task updates every 5 ticks
                if (ticks % 5 == 0) {
                    Iterator<ICraftingTask> craftingTaskIterator = craftingTasks.iterator();
                    Map<ICraftingPatternContainer, Integer> usedCrafters = new HashMap<>();

                    while (craftingTaskIterator.hasNext()) {
                        ICraftingTask task = craftingTaskIterator.next();

                        if (task.update(usedCrafters)) {
                            craftingTaskIterator.remove();

                            craftingTasksChanged = true;
                        } else if (!task.getMissing().isEmpty() && ticks % 100 == 0 && Math.random() > 0.5) {
                            task.getMissing().clear();
                        }
                    }

                    if (craftingTasksChanged) {
                        craftingMonitorUpdateRequested = true;
                    }
                }

                for (IReaderWriterChannel channel : readerWriterChannels.values()) {
                    for (IReaderWriterHandler handler : channel.getHandlers()) {
                        handler.update(channel);
                    }
                }

                if (!craftingTasks.isEmpty() || !readerWriterChannels.isEmpty()) {
                    markDirty();
                }

                if (craftingMonitorUpdateRequested) {
                    craftingMonitorUpdateRequested = false;

                    sendCraftingMonitorUpdate();
                }
            }

            networkItemHandler.update();

            if (getType() == EnumControllerType.NORMAL) {
                if (!RS.INSTANCE.config.controllerUsesEnergy) {
                    energy.setEnergyStored(energy.getMaxEnergyStored());
                } else if (energy.getEnergyStored() - getEnergyUsage() >= 0) {
                    energy.extractEnergyInternal(getEnergyUsage(), false);
                } else {
                    energy.setEnergyStored(0);
                }
            } else if (getType() == EnumControllerType.CREATIVE) {
                energy.setEnergyStored(energy.getMaxEnergyStored());
            }

            if (couldRun != canRun()) {
                couldRun = canRun();

                nodeGraph.rebuild();
                securityManager.rebuild();
            }

            if (getEnergyScaledForDisplay() != lastEnergyDisplay) {
                lastEnergyDisplay = getEnergyScaledForDisplay();

                RSUtils.updateBlock(world, pos);
            }
        }

        super.update();
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
    public INetworkItemHandler getNetworkItemHandler() {
        return networkItemHandler;
    }

    @Override
    public void invalidate() {
        super.invalidate();

        if (getWorld() != null && !getWorld().isRemote) {
            nodeGraph.disconnectAll();
        }
    }

    @Override
    public IStorageCache<ItemStack> getItemStorageCache() {
        return itemStorage;
    }

    @Override
    public IStorageCache<FluidStack> getFluidStorageCache() {
        return fluidStorage;
    }

    @Override
    public List<ICraftingTask> getCraftingTasks() {
        return craftingTasks;
    }

    @Override
    public void addCraftingTask(@Nonnull ICraftingTask task) {
        craftingTasksToAdd.add(task);

        markDirty();
    }

    @Override
    public void cancelCraftingTask(@Nonnull ICraftingTask task) {
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

        IStackList<ItemStack> itemList = itemStorage.getList().getOredicted();

        for (int i = 0; i < patterns.size(); ++i) {
            int score = 0;

            for (ItemStack input : patterns.get(i).getInputs()) {
                if (input != null) {
                    ItemStack stored = itemList.get(input, IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT | (patterns.get(i).isOredict() ? IComparer.COMPARE_OREDICT : 0));

                    score += stored != null ? stored.getCount() : 0;
                }
            }

            if (score > highestScore) {
                highestScore = score;
                highestPattern = i;
            }
        }

        return patterns.get(highestPattern);
    }

    @Override
    public void scheduleCraftingTask(ItemStack stack, int toSchedule, int compare) {
        for (ICraftingTask task : getCraftingTasks()) {
            for (ItemStack output : task.getPattern().getOutputs()) {
                if (API.instance().getComparer().isEqual(output, stack, compare)) {
                    toSchedule -= output.getCount() * task.getQuantity();
                }
            }
        }

        if (toSchedule > 0) {
            ICraftingPattern pattern = getPattern(stack, compare);

            if (pattern != null) {
                ICraftingTask task = createCraftingTask(stack, pattern, toSchedule);

                task.calculate();
                task.getMissing().clear();

                addCraftingTask(task);

                markCraftingMonitorForUpdate();
            }
        }
    }

    @Override
    public void rebuildPatterns() {
        patterns.clear();

        for (INetworkNode node : nodeGraph.all()) {
            if (node instanceof ICraftingPatternContainer && node.canUpdate()) {
                patterns.addAll(((ICraftingPatternContainer) node).getPatterns());
            }
        }
    }

    @Override
    public void sendItemStorageToClient() {
        getWorld().getMinecraftServer().getPlayerList().getPlayers().stream()
                .filter(player -> isWatchingGrid(player, EnumGridType.NORMAL, EnumGridType.CRAFTING, EnumGridType.PATTERN))
                .forEach(this::sendItemStorageToClient);
    }

    @Override
    public void sendItemStorageToClient(EntityPlayerMP player) {
        RS.INSTANCE.network.sendTo(new MessageGridItemUpdate(this, securityManager.hasPermission(Permission.AUTOCRAFTING, player)), player);
    }

    @Override
    public void sendItemStorageDeltaToClient(ItemStack stack, int delta) {
        getWorld().getMinecraftServer().getPlayerList().getPlayers().stream()
                .filter(player -> isWatchingGrid(player, EnumGridType.NORMAL, EnumGridType.CRAFTING, EnumGridType.PATTERN))
                .forEach(player -> RS.INSTANCE.network.sendTo(new MessageGridItemDelta(this, stack, delta), player));
    }

    @Override
    public void sendFluidStorageToClient() {
        getWorld().getMinecraftServer().getPlayerList().getPlayers().stream()
                .filter(player -> isWatchingGrid(player, EnumGridType.FLUID))
                .forEach(this::sendFluidStorageToClient);
    }

    @Override
    public void sendFluidStorageToClient(EntityPlayerMP player) {
        RS.INSTANCE.network.sendTo(new MessageGridFluidUpdate(this, securityManager.hasPermission(Permission.AUTOCRAFTING, player)), player);
    }

    @Override
    public void sendFluidStorageDeltaToClient(FluidStack stack, int delta) {
        getWorld().getMinecraftServer().getPlayerList().getPlayers().stream()
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
    public void markCraftingMonitorForUpdate() {
        craftingMonitorUpdateRequested = true;
    }

    @Override
    public void sendCraftingMonitorUpdate() {
        List<EntityPlayerMP> watchers = getWorld().getMinecraftServer().getPlayerList().getPlayers().stream()
                .filter(player -> player.openContainer instanceof ContainerCraftingMonitor && pos.equals(((ContainerCraftingMonitor) player.openContainer).getCraftingMonitor().getNetworkPosition()))
                .collect(Collectors.toList());

        if (!watchers.isEmpty()) {
            List<ICraftingMonitorElement> elements = getElements();

            watchers.forEach(player -> RS.INSTANCE.network.sendTo(new MessageCraftingMonitorElements(elements), player));
        }
    }

    @Override
    public void sendCraftingMonitorUpdate(EntityPlayerMP player) {
        RS.INSTANCE.network.sendTo(new MessageCraftingMonitorElements(getElements()), player);
    }

    @Nullable
    @Override
    public IReaderWriterChannel getReaderWriterChannel(String name) {
        return readerWriterChannels.get(name);
    }

    @Override
    public void addReaderWriterChannel(String name) {
        readerWriterChannels.put(name, API.instance().createReaderWriterChannel(name, this));

        sendReaderWriterChannelUpdate();
    }

    @Override
    public void removeReaderWriterChannel(String name) {
        IReaderWriterChannel channel = getReaderWriterChannel(name);

        if (channel != null) {
            channel.getReaders().forEach(reader -> reader.setChannel(""));
            channel.getWriters().forEach(writer -> writer.setChannel(""));

            readerWriterChannels.remove(name);

            sendReaderWriterChannelUpdate();
        }
    }

    @Override
    public void sendReaderWriterChannelUpdate() {
        getWorld().getMinecraftServer().getPlayerList().getPlayers().stream()
                .filter(player -> player.openContainer instanceof ContainerReaderWriter &&
                        ((ContainerReaderWriter) player.openContainer).getReaderWriter().getNetwork() != null &&
                        pos.equals(((ContainerReaderWriter) player.openContainer).getReaderWriter().getNetwork().getPosition()))
                .forEach(this::sendReaderWriterChannelUpdate);
    }

    @Override
    public void sendReaderWriterChannelUpdate(EntityPlayerMP player) {
        RS.INSTANCE.network.sendTo(new MessageReaderWriterUpdate(readerWriterChannels.keySet()), player);
    }

    private List<ICraftingMonitorElement> getElements() {
        return craftingTasks.stream().flatMap(t -> t.getCraftingMonitorElements().stream()).collect(Collectors.toList());
    }

    @Override
    public ItemStack insertItem(@Nonnull ItemStack stack, int size, boolean simulate) {
        if (stack.isEmpty() || itemStorage.getStorages().isEmpty()) {
            return ItemHandlerHelper.copyStackWithSize(stack, size);
        }

        ItemStack remainder = stack;

        int inserted = 0;
        int insertedExternally = 0;

        for (IStorage<ItemStack> storage : this.itemStorage.getStorages()) {
            if (storage.getAccessType() == AccessType.EXTRACT) {
                continue;
            }

            int storedPre = storage.getStored();

            remainder = storage.insert(remainder, size, simulate);

            if (!simulate) {
                inserted += storage.getCacheDelta(storedPre, size, remainder);
            }

            if (remainder == null) {
                // The external storage is responsible for sending changes, we don't need to anymore
                if (storage instanceof StorageItemExternal && !simulate) {
                    ((StorageItemExternal) storage).detectChanges(this);

                    insertedExternally += size;
                }

                break;
            } else {
                // The external storage is responsible for sending changes, we don't need to anymore
                if (size != remainder.getCount() && storage instanceof StorageItemExternal && !simulate) {
                    ((StorageItemExternal) storage).detectChanges(this);

                    insertedExternally += size - remainder.getCount();
                }

                size = remainder.getCount();
            }
        }

        if (!simulate) {
            if (inserted - insertedExternally > 0) {
                itemStorage.add(stack, inserted - insertedExternally, false);
            }

            if (inserted > 0) {
                ItemStack checkSteps = ItemHandlerHelper.copyStackWithSize(stack, inserted);

                for (ICraftingTask task : craftingTasks) {
                    for (ICraftingStep processable : task.getSteps()) {
                        if (processable.onReceiveOutput(checkSteps)) {
                            return remainder; // All done
                        }
                    }
                }
            }
        }

        return remainder;
    }

    @Override
    public ItemStack extractItem(@Nonnull ItemStack stack, int size, int flags, boolean simulate) {
        int requested = size;
        int received = 0;

        int extractedExternally = 0;

        ItemStack newStack = null;

        for (IStorage<ItemStack> storage : this.itemStorage.getStorages()) {
            ItemStack took = null;

            if (storage.getAccessType() != AccessType.INSERT) {
                took = storage.extract(stack, requested - received, flags, simulate);
            }

            if (took != null) {
                // The external storage is responsible for sending changes, we don't need to anymore
                if (storage instanceof StorageItemExternal && !simulate) {
                    ((StorageItemExternal) storage).detectChanges(this);

                    extractedExternally += took.getCount();
                }

                if (newStack == null) {
                    newStack = took;
                } else {
                    newStack.grow(took.getCount());
                }

                received += took.getCount();
            }

            if (requested == received) {
                break;
            }
        }

        if (newStack != null && newStack.getCount() - extractedExternally > 0 && !simulate) {
            itemStorage.remove(newStack, newStack.getCount() - extractedExternally);
        }

        return newStack;
    }

    @Nullable
    @Override
    public FluidStack insertFluid(@Nonnull FluidStack stack, int size, boolean simulate) {
        if (fluidStorage.getStorages().isEmpty()) {
            return RSUtils.copyStackWithSize(stack, size);
        }

        FluidStack remainder = stack;

        int inserted = 0;

        for (IStorage<FluidStack> storage : this.fluidStorage.getStorages()) {
            if (storage.getAccessType() == AccessType.EXTRACT) {
                continue;
            }

            int storedPre = storage.getStored();

            remainder = storage.insert(remainder, size, simulate);

            if (!simulate) {
                inserted += storage.getCacheDelta(storedPre, size, remainder);
            }

            if (storage instanceof StorageFluidExternal && !simulate) {
                ((StorageFluidExternal) storage).updateCacheForcefully();
            }

            if (remainder == null) {
                break;
            } else {
                size = remainder.amount;
            }
        }

        if (inserted > 0) {
            fluidStorage.add(stack, inserted, false);
        }

        return remainder;
    }

    @Nullable
    @Override
    public FluidStack extractFluid(@Nonnull FluidStack stack, int size, int flags, boolean simulate) {
        int requested = size;
        int received = 0;

        FluidStack newStack = null;

        for (IStorage<FluidStack> storage : this.fluidStorage.getStorages()) {
            FluidStack took = null;

            if (storage.getAccessType() != AccessType.INSERT) {
                took = storage.extract(stack, requested - received, flags, simulate);
            }

            if (took != null) {
                if (storage instanceof StorageFluidExternal && !simulate) {
                    ((StorageFluidExternal) storage).updateCacheForcefully();
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

        if (newStack != null && !simulate) {
            fluidStorage.remove(newStack, newStack.amount);
        }

        return newStack;
    }

    @Override
    public World getNetworkWorld() {
        return getWorld();
    }

    private static ICraftingTask readCraftingTask(World world, INetworkMaster network, NBTTagCompound tag) {
        ItemStack stack = new ItemStack(tag.getCompoundTag(ICraftingTask.NBT_PATTERN_STACK));

        if (!stack.isEmpty() && stack.getItem() instanceof ICraftingPatternProvider) {
            TileEntity container = world.getTileEntity(BlockPos.fromLong(tag.getLong(ICraftingTask.NBT_PATTERN_CONTAINER)));

            if (container instanceof ICraftingPatternContainer) {
                ICraftingPattern pattern = ((ICraftingPatternProvider) stack.getItem()).create(world, stack, (ICraftingPatternContainer) container);

                ICraftingTaskFactory factory = API.instance().getCraftingTaskRegistry().getFactory(tag.getString(ICraftingTask.NBT_PATTERN_ID));

                if (factory != null) {
                    return factory.create(world, network, tag.hasKey(ICraftingTask.NBT_REQUESTED) ? new ItemStack(tag.getCompoundTag(ICraftingTask.NBT_REQUESTED)) : null, pattern, tag.getInteger(ICraftingTask.NBT_QUANTITY), tag);
                }
            }
        }

        return null;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);

        if (tag.hasKey(NBT_ENERGY)) {
            energy.setEnergyStored(tag.getInteger(NBT_ENERGY));
        }

        redstoneMode = RedstoneMode.read(tag);

        if (tag.hasKey(NBT_CRAFTING_TASKS)) {
            NBTTagList taskList = tag.getTagList(NBT_CRAFTING_TASKS, Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < taskList.tagCount(); ++i) {
                craftingTasksToRead.add(taskList.getCompoundTagAt(i));
            }
        }

        if (tag.hasKey(NBT_READER_WRITER_CHANNELS)) {
            NBTTagList readerWriterChannelsList = tag.getTagList(NBT_READER_WRITER_CHANNELS, Constants.NBT.TAG_COMPOUND);

            for (int i = 0; i < readerWriterChannelsList.tagCount(); ++i) {
                NBTTagCompound channelTag = readerWriterChannelsList.getCompoundTagAt(i);

                String name = channelTag.getString(NBT_READER_WRITER_NAME);

                IReaderWriterChannel channel = API.instance().createReaderWriterChannel(name, this);

                channel.readFromNBT(channelTag);

                readerWriterChannels.put(name, channel);
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);

        tag.setInteger(NBT_ENERGY, energy.getEnergyStored());

        redstoneMode.write(tag);

        NBTTagList craftingTaskList = new NBTTagList();

        for (ICraftingTask task : craftingTasks) {
            craftingTaskList.appendTag(task.writeToNBT(new NBTTagCompound()));
        }

        tag.setTag(NBT_CRAFTING_TASKS, craftingTaskList);

        NBTTagList readerWriterChannelsList = new NBTTagList();

        for (Map.Entry<String, IReaderWriterChannel> entry : readerWriterChannels.entrySet()) {
            NBTTagCompound channelTag = entry.getValue().writeToNBT(new NBTTagCompound());

            channelTag.setString(NBT_READER_WRITER_NAME, entry.getKey());

            readerWriterChannelsList.appendTag(channelTag);
        }

        tag.setTag(NBT_READER_WRITER_CHANNELS, readerWriterChannelsList);

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
        energy.setMaxEnergyStored(tag.getInteger(NBT_ENERGY_CAPACITY));
        energy.setEnergyStored(tag.getInteger(NBT_ENERGY));

        super.readUpdate(tag);
    }

    public static int getEnergyScaled(int stored, int capacity, int scale) {
        return (int) ((float) stored / (float) capacity * (float) scale);
    }

    public int getEnergyScaledForDisplay() {
        return getEnergyScaled(energy.getEnergyStored(), energy.getMaxEnergyStored(), 7);
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

    @Nonnull
    @Override
    public ItemStack getItemStack() {
        IBlockState state = getWorld().getBlockState(pos);

        Item item = Item.getItemFromBlock(state.getBlock());

        return new ItemStack(item, 1, state.getBlock().getMetaFromState(state));
    }

    @Override
    public void onConnected(INetworkMaster network) {
        Preconditions.checkArgument(this == network, "Should not be connected to another controller");
    }

    @Override
    public void onDisconnected(INetworkMaster network) {
        Preconditions.checkArgument(this == network, "Should not be connected to another controller");
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public INetworkMaster getNetwork() {
        return this;
    }

    public EnumControllerType getType() {
        if (type == null && getWorld().getBlockState(pos).getBlock() == RSBlocks.CONTROLLER) {
            this.type = (EnumControllerType) getWorld().getBlockState(pos).getValue(BlockController.TYPE);
        }

        return type == null ? EnumControllerType.NORMAL : type;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(energy);
        }

        if (energyTesla != null) {
            if (capability == TeslaCapabilities.CAPABILITY_HOLDER) {
                return TeslaCapabilities.CAPABILITY_HOLDER.cast(energyTesla);
            }
            if (capability == TeslaCapabilities.CAPABILITY_CONSUMER) {
                return TeslaCapabilities.CAPABILITY_CONSUMER.cast(energyTesla);
            }
        }

        if (capability == CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY) {
            return CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY.cast(this);
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY
                || (energyTesla != null && (capability == TeslaCapabilities.CAPABILITY_HOLDER || capability == TeslaCapabilities.CAPABILITY_CONSUMER))
                || capability == CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY
                || super.hasCapability(capability, facing);
    }

    @Override
    public INetworkNode getNode() {
        return this;
    }

    @Override
    public INetworkNode createNode() {
        return this;
    }
}
