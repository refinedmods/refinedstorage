package com.raoulvdberge.refinedstorage.tile;

import com.google.common.base.Preconditions;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingManager;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.INetworkNodeGraph;
import com.raoulvdberge.refinedstorage.api.network.grid.IFluidGridHandler;
import com.raoulvdberge.refinedstorage.api.network.grid.IItemGridHandler;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemHandler;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterChannel;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterHandler;
import com.raoulvdberge.refinedstorage.api.network.security.ISecurityManager;
import com.raoulvdberge.refinedstorage.api.network.security.Permission;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCache;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.CraftingManager;
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
import com.raoulvdberge.refinedstorage.block.ControllerType;
import com.raoulvdberge.refinedstorage.block.GridType;
import com.raoulvdberge.refinedstorage.capability.CapabilityNetworkNodeProxy;
import com.raoulvdberge.refinedstorage.container.ContainerCraftingMonitor;
import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import com.raoulvdberge.refinedstorage.container.ContainerReaderWriter;
import com.raoulvdberge.refinedstorage.integration.forgeenergy.EnergyForge;
import com.raoulvdberge.refinedstorage.network.*;
import com.raoulvdberge.refinedstorage.tile.config.IRedstoneConfigurable;
import com.raoulvdberge.refinedstorage.tile.config.RedstoneMode;
import com.raoulvdberge.refinedstorage.tile.data.RSSerializers;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import com.raoulvdberge.refinedstorage.tile.grid.IGrid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
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
import java.util.function.Predicate;

public class TileController extends TileBase implements ITickable, INetwork, IRedstoneConfigurable, INetworkNode, INetworkNodeProxy<TileController> {
    private static final Comparator<ClientNode> CLIENT_NODE_COMPARATOR = (left, right) -> {
        if (left.getEnergyUsage() == right.getEnergyUsage()) {
            return 0;
        }

        return (left.getEnergyUsage() > right.getEnergyUsage()) ? -1 : 1;
    };

    public static final TileDataParameter<Integer, TileController> REDSTONE_MODE = RedstoneMode.createParameter();
    public static final TileDataParameter<Integer, TileController> ENERGY_USAGE = new TileDataParameter<>(DataSerializers.VARINT, 0, TileController::getEnergyUsage);
    public static final TileDataParameter<Integer, TileController> ENERGY_STORED = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> t.getEnergy().getEnergyStored());
    public static final TileDataParameter<Integer, TileController> ENERGY_CAPACITY = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> t.getEnergy().getMaxEnergyStored());
    public static final TileDataParameter<List<ClientNode>, TileController> NODES = new TileDataParameter<>(RSSerializers.CLIENT_NODE_SERIALIZER, new ArrayList<>(), t -> {
        List<ClientNode> nodes = new ArrayList<>();

        for (INetworkNode node : t.nodeGraph.all()) {
            if (node.canUpdate()) {
                ItemStack stack = node.getItemStack();

                if (stack.isEmpty()) {
                    continue;
                }

                ClientNode clientNode = new ClientNode(stack, 1, node.getEnergyUsage());

                if (nodes.contains(clientNode)) {
                    ClientNode other = nodes.get(nodes.indexOf(clientNode));

                    other.setAmount(other.getAmount() + 1);
                } else {
                    nodes.add(clientNode);
                }
            }
        }

        nodes.sort(CLIENT_NODE_COMPARATOR);

        return nodes;
    });

    private static final int THROTTLE_INACTIVE_TO_ACTIVE = 20;
    private static final int THROTTLE_ACTIVE_TO_INACTIVE = 4;

    public static final String NBT_ENERGY = "Energy";
    public static final String NBT_ENERGY_CAPACITY = "EnergyCapacity";

    private static final String NBT_READER_WRITER_CHANNELS = "ReaderWriterChannels";
    private static final String NBT_READER_WRITER_NAME = "Name";

    private IItemGridHandler itemGridHandler = new ItemGridHandler(this);
    private IFluidGridHandler fluidGridHandler = new FluidGridHandler(this);

    private INetworkItemHandler networkItemHandler = new NetworkItemHandler(this);

    private INetworkNodeGraph nodeGraph = new NetworkNodeGraph(this);

    private ICraftingManager craftingManager = new CraftingManager(this);

    private ISecurityManager securityManager = new SecurityManager(this);

    private IStorageCache<ItemStack> itemStorage = new StorageCacheItem(this);
    private IStorageCache<FluidStack> fluidStorage = new StorageCacheFluid(this);

    private Map<String, IReaderWriterChannel> readerWriterChannels = new HashMap<>();

    private EnergyForge energy = new EnergyForge(RS.INSTANCE.config.controllerCapacity);

    private int lastEnergyDisplay;

    private boolean couldRun;
    private int ticksSinceUpdateChanged;

    private boolean craftingMonitorUpdateRequested;

    private ControllerType type;

    private RedstoneMode redstoneMode = RedstoneMode.IGNORE;

    public TileController() {
        dataManager.addWatchedParameter(REDSTONE_MODE);
        dataManager.addWatchedParameter(ENERGY_USAGE);
        dataManager.addWatchedParameter(ENERGY_STORED);
        dataManager.addParameter(ENERGY_CAPACITY);
        dataManager.addParameter(NODES);
    }

    public EnergyForge getEnergy() {
        return energy;
    }

    @Override
    public BlockPos getPosition() {
        return pos;
    }

    @Override
    public boolean canRun() {
        return energy.getEnergyStored() > 0 && redstoneMode.isEnabled(world, pos);
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
    public ICraftingManager getCraftingManager() {
        return craftingManager;
    }

    @Override
    public void update() {
        if (!world.isRemote) {
            if (canRun()) {
                craftingManager.update();

                for (IReaderWriterChannel channel : readerWriterChannels.values()) {
                    for (IReaderWriterHandler handler : channel.getHandlers()) {
                        handler.update(channel);
                    }
                }

                if (!craftingManager.getTasks().isEmpty() || !readerWriterChannels.isEmpty()) {
                    markDirty();
                }

                if (craftingMonitorUpdateRequested) {
                    craftingMonitorUpdateRequested = false;

                    sendCraftingMonitorUpdate();
                }
            }

            networkItemHandler.update();

            if (getType() == ControllerType.NORMAL) {
                if (!RS.INSTANCE.config.controllerUsesEnergy) {
                    energy.setEnergyStored(energy.getMaxEnergyStored());
                } else if (energy.getEnergyStored() - getEnergyUsage() >= 0) {
                    energy.extractEnergyInternal(getEnergyUsage());
                } else {
                    energy.setEnergyStored(0);
                }
            } else if (getType() == ControllerType.CREATIVE) {
                energy.setEnergyStored(energy.getMaxEnergyStored());
            }

            boolean canRun = canRun();

            if (couldRun != canRun) {
                ++ticksSinceUpdateChanged;

                if (canRun ? (ticksSinceUpdateChanged > THROTTLE_INACTIVE_TO_ACTIVE) : (ticksSinceUpdateChanged > THROTTLE_ACTIVE_TO_INACTIVE)) {
                    ticksSinceUpdateChanged = 0;
                    couldRun = canRun;

                    nodeGraph.rebuild();
                    securityManager.rebuild();
                }
            } else {
                ticksSinceUpdateChanged = 0;
            }

            int energyDisplay = getEnergyScaledForDisplay();

            if (lastEnergyDisplay != energyDisplay) {
                lastEnergyDisplay = energyDisplay;

                RSUtils.updateBlock(world, pos);
            }
        }
    }

    @Override
    public String getId() {
        return null;
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

        if (world != null && !world.isRemote) {
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
    public void sendItemStorageToClient() {
        world.getMinecraftServer().getPlayerList().getPlayers().stream()
            .filter(player -> isWatchingGrid(player, GridType.NORMAL, GridType.CRAFTING, GridType.PATTERN))
            .forEach(this::sendItemStorageToClient);
    }

    @Override
    public void sendItemStorageToClient(EntityPlayerMP player) {
        RS.INSTANCE.network.sendTo(new MessageGridItemUpdate(this, securityManager.hasPermission(Permission.AUTOCRAFTING, player)), player);
    }

    @Override
    public void sendItemStorageDeltaToClient(ItemStack stack, int delta) {
        world.getMinecraftServer().getPlayerList().getPlayers().stream()
            .filter(player -> isWatchingGrid(player, GridType.NORMAL, GridType.CRAFTING, GridType.PATTERN))
            .forEach(player -> RS.INSTANCE.network.sendTo(new MessageGridItemDelta(this, stack, delta), player));
    }

    @Override
    public void sendFluidStorageToClient() {
        world.getMinecraftServer().getPlayerList().getPlayers().stream()
            .filter(player -> isWatchingGrid(player, GridType.FLUID))
            .forEach(this::sendFluidStorageToClient);
    }

    @Override
    public void sendFluidStorageToClient(EntityPlayerMP player) {
        RS.INSTANCE.network.sendTo(new MessageGridFluidUpdate(this, securityManager.hasPermission(Permission.AUTOCRAFTING, player)), player);
    }

    @Override
    public void sendFluidStorageDeltaToClient(FluidStack stack, int delta) {
        world.getMinecraftServer().getPlayerList().getPlayers().stream()
            .filter(player -> isWatchingGrid(player, GridType.FLUID))
            .forEach(player -> RS.INSTANCE.network.sendTo(new MessageGridFluidDelta(stack, delta), player));
    }

    private boolean isWatchingGrid(EntityPlayer player, GridType... types) {
        if (player.openContainer.getClass() == ContainerGrid.class) {
            IGrid grid = ((ContainerGrid) player.openContainer).getGrid();

            if (grid.getNetwork() != null && pos.equals(grid.getNetwork().getPosition())) {
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
        world.getMinecraftServer().getPlayerList().getPlayers().stream()
            .filter(player -> player.openContainer instanceof ContainerCraftingMonitor && pos.equals(((ContainerCraftingMonitor) player.openContainer).getCraftingMonitor().getNetworkPosition()))
            .forEach(player -> RS.INSTANCE.network.sendTo(new MessageCraftingMonitorElements(((ContainerCraftingMonitor) player.openContainer).getCraftingMonitor()), player));
    }

    @Override
    public void sendCraftingMonitorUpdate(EntityPlayerMP player) {
        RS.INSTANCE.network.sendTo(new MessageCraftingMonitorElements(((ContainerCraftingMonitor) player.openContainer).getCraftingMonitor()), player);
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
        world.getMinecraftServer().getPlayerList().getPlayers().stream()
            .filter(player -> player.openContainer instanceof ContainerReaderWriter &&
                ((ContainerReaderWriter) player.openContainer).getReaderWriter().getNetwork() != null &&
                pos.equals(((ContainerReaderWriter) player.openContainer).getReaderWriter().getNetwork().getPosition()))
            .forEach(this::sendReaderWriterChannelUpdate);
    }

    @Override
    public void sendReaderWriterChannelUpdate(EntityPlayerMP player) {
        RS.INSTANCE.network.sendTo(new MessageReaderWriterUpdate(readerWriterChannels.keySet()), player);
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

        if (!simulate && inserted - insertedExternally > 0) {
            itemStorage.add(stack, inserted - insertedExternally, false);
        }

        return remainder;
    }

    @Override
    public ItemStack extractItem(@Nonnull ItemStack stack, int size, int flags, boolean simulate, Predicate<IStorage<ItemStack>> filter) {
        int requested = size;
        int received = 0;

        int extractedExternally = 0;

        ItemStack newStack = null;

        for (IStorage<ItemStack> storage : this.itemStorage.getStorages()) {
            ItemStack took = null;

            if (filter.test(storage) && storage.getAccessType() != AccessType.INSERT) {
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
    public World world() {
        return world;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);

        if (tag.hasKey(NBT_ENERGY)) {
            energy.setEnergyStored(tag.getInteger(NBT_ENERGY));
        }

        redstoneMode = RedstoneMode.read(tag);

        craftingManager.readFromNBT(tag);

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

        craftingManager.writeToNBT(tag);

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
        IBlockState state = world.getBlockState(pos);

        Item item = Item.getItemFromBlock(state.getBlock());

        return new ItemStack(item, 1, state.getBlock().getMetaFromState(state));
    }

    @Override
    public void onConnected(INetwork network) {
        Preconditions.checkArgument(this == network, "Should not be connected to another controller");
    }

    @Override
    public void onDisconnected(INetwork network) {
        Preconditions.checkArgument(this == network, "Should not be connected to another controller");
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public INetwork getNetwork() {
        return this;
    }

    public ControllerType getType() {
        if (type == null && world.getBlockState(pos).getBlock() == RSBlocks.CONTROLLER) {
            this.type = (ControllerType) world.getBlockState(pos).getValue(BlockController.TYPE);
        }

        return type == null ? ControllerType.NORMAL : type;
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(energy);
        }

        if (capability == CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY) {
            return CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY.cast(this);
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY
            || capability == CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY
            || super.hasCapability(capability, facing);
    }

    @Override
    @Nonnull
    public TileController getNode() {
        return this;
    }
}
