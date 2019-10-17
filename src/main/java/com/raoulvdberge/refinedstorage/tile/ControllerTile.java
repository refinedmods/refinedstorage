package com.raoulvdberge.refinedstorage.tile;

import com.google.common.base.Preconditions;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSTiles;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingManager;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.INetworkNodeGraph;
import com.raoulvdberge.refinedstorage.api.network.INetworkNodeVisitor;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IFluidGridHandler;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IItemGridHandler;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemHandler;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;
import com.raoulvdberge.refinedstorage.api.network.security.ISecurityManager;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.storage.cache.IStorageCache;
import com.raoulvdberge.refinedstorage.api.storage.externalstorage.IExternalStorage;
import com.raoulvdberge.refinedstorage.api.storage.tracker.IStorageTracker;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.CraftingManager;
import com.raoulvdberge.refinedstorage.apiimpl.network.NetworkNodeGraph;
import com.raoulvdberge.refinedstorage.apiimpl.network.grid.handler.FluidGridHandler;
import com.raoulvdberge.refinedstorage.apiimpl.network.grid.handler.ItemGridHandler;
import com.raoulvdberge.refinedstorage.apiimpl.network.item.NetworkItemHandler;
import com.raoulvdberge.refinedstorage.apiimpl.network.security.SecurityManager;
import com.raoulvdberge.refinedstorage.apiimpl.storage.cache.FluidStorageCache;
import com.raoulvdberge.refinedstorage.apiimpl.storage.cache.ItemStorageCache;
import com.raoulvdberge.refinedstorage.apiimpl.storage.tracker.FluidStorageTracker;
import com.raoulvdberge.refinedstorage.apiimpl.storage.tracker.ItemStorageTracker;
import com.raoulvdberge.refinedstorage.block.ControllerBlock;
import com.raoulvdberge.refinedstorage.energy.BaseEnergyStorage;
import com.raoulvdberge.refinedstorage.tile.config.IRedstoneConfigurable;
import com.raoulvdberge.refinedstorage.tile.config.RedstoneMode;
import com.raoulvdberge.refinedstorage.tile.data.RSSerializers;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import static com.raoulvdberge.refinedstorage.capability.NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY;

// TODO: Change INetwork to be offloaded from the tile.
public class ControllerTile extends BaseTile implements ITickableTileEntity, INetwork, IRedstoneConfigurable, INetworkNode, INetworkNodeProxy<ControllerTile>, INetworkNodeVisitor {
    private static final Comparator<ClientNode> CLIENT_NODE_COMPARATOR = (left, right) -> {
        if (left.getEnergyUsage() == right.getEnergyUsage()) {
            return 0;
        }

        return (left.getEnergyUsage() > right.getEnergyUsage()) ? -1 : 1;
    };

    public static final TileDataParameter<Integer, ControllerTile> REDSTONE_MODE = RedstoneMode.createParameter();
    public static final TileDataParameter<Integer, ControllerTile> ENERGY_USAGE = new TileDataParameter<>(DataSerializers.VARINT, 0, ControllerTile::getEnergyUsage);
    public static final TileDataParameter<Integer, ControllerTile> ENERGY_STORED = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> t.energy.getEnergyStored());
    public static final TileDataParameter<Integer, ControllerTile> ENERGY_CAPACITY = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> t.energy.getMaxEnergyStored());
    public static final TileDataParameter<List<ClientNode>, ControllerTile> NODES = new TileDataParameter<>(RSSerializers.CLIENT_NODE_SERIALIZER, new ArrayList<>(), t -> {
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
    private static final String NBT_ENERGY_TYPE = "EnergyType";

    private static final String NBT_ITEM_STORAGE_TRACKER = "ItemStorageTracker";
    private static final String NBT_FLUID_STORAGE_TRACKER = "FluidStorageTracker";

    private IItemGridHandler itemGridHandler = new ItemGridHandler(this);
    private IFluidGridHandler fluidGridHandler = new FluidGridHandler(this);

    private INetworkItemHandler networkItemHandler = new NetworkItemHandler(this);

    private INetworkNodeGraph nodeGraph = new NetworkNodeGraph(this);

    private ICraftingManager craftingManager = new CraftingManager(this);

    private ISecurityManager securityManager = new SecurityManager(this);

    private IStorageCache<ItemStack> itemStorage = new ItemStorageCache(this);
    private ItemStorageTracker itemStorageTracker = new ItemStorageTracker(this::markDirty);

    private IStorageCache<FluidStack> fluidStorage = new FluidStorageCache(this);
    private FluidStorageTracker fluidStorageTracker = new FluidStorageTracker(this::markDirty);

    private final BaseEnergyStorage energy = new BaseEnergyStorage(RS.SERVER_CONFIG.getController().getCapacity(), RS.SERVER_CONFIG.getController().getMaxTransfer());

    private final LazyOptional<IEnergyStorage> energyProxyCap = LazyOptional.of(() -> energy);
    private final LazyOptional<INetworkNodeProxy<ControllerTile>> networkNodeProxyCap = LazyOptional.of(() -> this);

    private boolean throttlingDisabled = true; // Will be enabled after first update
    private boolean couldRun;
    private int ticksSinceUpdateChanged;

    private ControllerBlock.Type type;
    private ControllerBlock.EnergyType lastEnergyType = ControllerBlock.EnergyType.OFF;

    private RedstoneMode redstoneMode = RedstoneMode.IGNORE;

    public ControllerTile(ControllerBlock.Type type) {
        super(type == ControllerBlock.Type.CREATIVE ? RSTiles.CREATIVE_CONTROLLER : RSTiles.CONTROLLER);

        this.type = type;

        dataManager.addWatchedParameter(REDSTONE_MODE);
        dataManager.addWatchedParameter(ENERGY_USAGE);
        dataManager.addWatchedParameter(ENERGY_STORED);
        dataManager.addParameter(ENERGY_CAPACITY);
        dataManager.addParameter(NODES);

        nodeGraph.addListener(() -> dataManager.sendParameterToWatchers(ControllerTile.NODES));
    }

    @Override
    public BlockPos getPosition() {
        return pos;
    }

    @Override
    public boolean canRun() {
        return this.energy.getEnergyStored() > 0 && redstoneMode.isEnabled(world, pos);
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
    public void tick() {
        if (!world.isRemote) {
            if (canRun()) {
                craftingManager.update();

                if (!craftingManager.getTasks().isEmpty()) {
                    markDirty();
                }
            }

            if (type == ControllerBlock.Type.NORMAL) {
                if (!RS.SERVER_CONFIG.getController().getUseEnergy()) {
                    this.energy.setStored(this.energy.getMaxEnergyStored());
                } else if (this.energy.extractEnergy(getEnergyUsage(), true) >= 0) {
                    this.energy.extractEnergy(getEnergyUsage(), false);
                } else {
                    this.energy.setStored(0);
                }
            } else if (type == ControllerBlock.Type.CREATIVE) {
                this.energy.setStored(this.energy.getMaxEnergyStored());
            }

            boolean canRun = canRun();

            if (couldRun != canRun) {
                ++ticksSinceUpdateChanged;

                if ((canRun ? (ticksSinceUpdateChanged > THROTTLE_INACTIVE_TO_ACTIVE) : (ticksSinceUpdateChanged > THROTTLE_ACTIVE_TO_INACTIVE)) || throttlingDisabled) {
                    ticksSinceUpdateChanged = 0;
                    couldRun = canRun;
                    throttlingDisabled = false;

                    nodeGraph.invalidate(Action.PERFORM, world, pos);
                    securityManager.invalidate();
                }
            } else {
                ticksSinceUpdateChanged = 0;
            }

            ControllerBlock.EnergyType energyType = getEnergyType();

            if (lastEnergyType != energyType) {
                lastEnergyType = energyType;

                world.setBlockState(pos, world.getBlockState(pos).with(ControllerBlock.ENERGY_TYPE, energyType));
            }
        }
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
    public void remove() {
        super.remove();

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
    @Nonnull
    public ItemStack insertItem(@Nonnull ItemStack stack, int size, Action action) {
        if (stack.isEmpty()) {
            return stack;
        }

        if (itemStorage.getStorages().isEmpty()) {
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

            remainder = storage.insert(remainder, size, action);

            if (action == Action.PERFORM) {
                inserted += storage.getCacheDelta(storedPre, size, remainder);
            }

            if (remainder.isEmpty()) {
                // The external storage is responsible for sending changes, we don't need to anymore
                if (storage instanceof IExternalStorage && action == Action.PERFORM) {
                    ((IExternalStorage) storage).update(this);

                    insertedExternally += size;
                }

                break;
            } else {
                // The external storage is responsible for sending changes, we don't need to anymore
                if (size != remainder.getCount() && storage instanceof IExternalStorage && action == Action.PERFORM) {
                    ((IExternalStorage) storage).update(this);

                    insertedExternally += size - remainder.getCount();
                }

                size = remainder.getCount();
            }
        }

        if (action == Action.PERFORM && inserted - insertedExternally > 0) {
            itemStorage.add(stack, inserted - insertedExternally, false, false);
        }

        return remainder;
    }

    @Override
    @Nonnull
    public ItemStack extractItem(@Nonnull ItemStack stack, int size, int flags, Action action, Predicate<IStorage<ItemStack>> filter) {
        if (stack.isEmpty()) {
            return stack;
        }

        int requested = size;
        int received = 0;

        int extractedExternally = 0;

        ItemStack newStack = ItemStack.EMPTY;

        for (IStorage<ItemStack> storage : this.itemStorage.getStorages()) {
            ItemStack took = ItemStack.EMPTY;

            if (filter.test(storage) && storage.getAccessType() != AccessType.INSERT) {
                took = storage.extract(stack, requested - received, flags, action);
            }

            if (!took.isEmpty()) {
                // The external storage is responsible for sending changes, we don't need to anymore
                if (storage instanceof IExternalStorage && action == Action.PERFORM) {
                    ((IExternalStorage) storage).update(this);

                    extractedExternally += took.getCount();
                }

                if (newStack.isEmpty()) {
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

        if (newStack.getCount() - extractedExternally > 0 && action == Action.PERFORM) {
            itemStorage.remove(newStack, newStack.getCount() - extractedExternally, false);
        }

        return newStack;
    }


    @Override
    @Nonnull
    public FluidStack insertFluid(@Nonnull FluidStack stack, int size, Action action) {
        if (stack.isEmpty()) {
            return stack;
        }

        if (fluidStorage.getStorages().isEmpty()) {
            return StackUtils.copy(stack, size);
        }

        FluidStack remainder = stack;

        int inserted = 0;
        int insertedExternally = 0;

        for (IStorage<FluidStack> storage : this.fluidStorage.getStorages()) {
            if (storage.getAccessType() == AccessType.EXTRACT) {
                continue;
            }

            int storedPre = storage.getStored();

            remainder = storage.insert(remainder, size, action);

            if (action == Action.PERFORM) {
                inserted += storage.getCacheDelta(storedPre, size, remainder);
            }

            if (remainder.isEmpty()) {
                // The external storage is responsible for sending changes, we don't need to anymore
                if (storage instanceof IExternalStorage && action == Action.PERFORM) {
                    ((IExternalStorage) storage).update(this);

                    insertedExternally += size;
                }

                break;
            } else {
                // The external storage is responsible for sending changes, we don't need to anymore
                if (size != remainder.getAmount() && storage instanceof IExternalStorage && action == Action.PERFORM) {
                    ((IExternalStorage) storage).update(this);

                    insertedExternally += size - remainder.getAmount();
                }

                size = remainder.getAmount();
            }
        }

        if (action == Action.PERFORM && inserted - insertedExternally > 0) {
            fluidStorage.add(stack, inserted - insertedExternally, false, false);
        }

        return remainder;
    }

    @Override
    @Nonnull
    public FluidStack extractFluid(@Nonnull FluidStack stack, int size, int flags, Action action, Predicate<IStorage<FluidStack>> filter) {
        if (stack.isEmpty()) {
            return stack;
        }

        int requested = size;
        int received = 0;

        int extractedExternally = 0;

        FluidStack newStack = FluidStack.EMPTY;

        for (IStorage<FluidStack> storage : this.fluidStorage.getStorages()) {
            FluidStack took = FluidStack.EMPTY;

            if (filter.test(storage) && storage.getAccessType() != AccessType.INSERT) {
                took = storage.extract(stack, requested - received, flags, action);
            }

            if (!took.isEmpty()) {
                // The external storage is responsible for sending changes, we don't need to anymore
                if (storage instanceof IExternalStorage && action == Action.PERFORM) {
                    ((IExternalStorage) storage).update(this);

                    extractedExternally += took.getAmount();
                }

                if (newStack.isEmpty()) {
                    newStack = took;
                } else {
                    newStack.grow(took.getAmount());
                }

                received += took.getAmount();
            }

            if (requested == received) {
                break;
            }
        }

        if (newStack.getAmount() - extractedExternally > 0 && action == Action.PERFORM) {
            fluidStorage.remove(newStack, newStack.getAmount() - extractedExternally, false);
        }

        return newStack;
    }

    @Override
    public IStorageTracker<ItemStack> getItemStorageTracker() {
        return itemStorageTracker;
    }

    @Override
    public IStorageTracker<FluidStack> getFluidStorageTracker() {
        return fluidStorageTracker;
    }

    @Override
    public World world() {
        return world;
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);

        if (tag.contains(NBT_ENERGY)) {
            this.energy.setStored(tag.getInt(NBT_ENERGY));
        }

        redstoneMode = RedstoneMode.read(tag);

        craftingManager.readFromNbt(tag);

        if (tag.contains(NBT_ITEM_STORAGE_TRACKER)) {
            itemStorageTracker.readFromNbt(tag.getList(NBT_ITEM_STORAGE_TRACKER, Constants.NBT.TAG_COMPOUND));
        }

        if (tag.contains(NBT_FLUID_STORAGE_TRACKER)) {
            fluidStorageTracker.readFromNbt(tag.getList(NBT_FLUID_STORAGE_TRACKER, Constants.NBT.TAG_COMPOUND));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);

        tag.putInt(NBT_ENERGY, this.energy.getEnergyStored());

        redstoneMode.write(tag);

        craftingManager.writeToNbt(tag);

        tag.put(NBT_ITEM_STORAGE_TRACKER, itemStorageTracker.serializeNbt());
        tag.put(NBT_FLUID_STORAGE_TRACKER, fluidStorageTracker.serializeNbt());

        return tag;
    }

    @Override
    public ResourceLocation getId() {
        return null;
    }

    @Override
    public CompoundNBT writeUpdate(CompoundNBT tag) {
        super.writeUpdate(tag);

        tag.putInt(NBT_ENERGY_TYPE, getEnergyType().ordinal());

        return tag;
    }

    @Override
    public void readUpdate(CompoundNBT tag) {
        if (tag.contains(NBT_ENERGY_TYPE)) {
            world.setBlockState(pos, world.getBlockState(pos).with(ControllerBlock.ENERGY_TYPE, ControllerBlock.EnergyType.values()[tag.getInt(NBT_ENERGY_TYPE)]));
        }

        super.readUpdate(tag);
    }

    public static int getEnergyScaled(int stored, int capacity, int scale) {
        return (int) ((float) stored / (float) capacity * (float) scale);
    }

    private ControllerBlock.EnergyType getEnergyType() {
        if (!redstoneMode.isEnabled(world, pos)) {
            return ControllerBlock.EnergyType.OFF;
        }

        return getEnergyType(this.energy.getEnergyStored(), this.energy.getMaxEnergyStored());
    }

    public static ControllerBlock.EnergyType getEnergyType(int stored, int capacity) {
        int energy = getEnergyScaled(stored, capacity, 100);

        if (energy <= 0) {
            return ControllerBlock.EnergyType.OFF;
        } else if (energy <= 10) {
            return ControllerBlock.EnergyType.NEARLY_OFF;
        } else if (energy <= 20) {
            return ControllerBlock.EnergyType.NEARLY_ON;
        }

        return ControllerBlock.EnergyType.ON;
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
        int usage = RS.SERVER_CONFIG.getController().getBaseUsage();

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
        BlockState state = world.getBlockState(pos);

        @SuppressWarnings("deprecation")
        Item item = Item.getItemFromBlock(state.getBlock());

        return new ItemStack(item, 1);
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

    @Override
    public void update() {
        // This is update from INetworkNode
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction direction) {
        if (cap == CapabilityEnergy.ENERGY) {
            return energyProxyCap.cast();
        }

        if (cap == NETWORK_NODE_PROXY_CAPABILITY) {
            return networkNodeProxyCap.cast();
        }

        return super.getCapability(cap, direction);
    }

    @Override
    @Nonnull
    public ControllerTile getNode() {
        return this;
    }

    @Override
    public void visit(Operator operator) {
        for (Direction facing : Direction.values()) {
            operator.apply(world, pos.offset(facing), facing.getOpposite());
        }
    }

    // Cannot use API#getNetworkNodeHashCode or API#isNetworkNodeEqual: it will crash with a AbstractMethodError (getPos).
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ControllerTile)) {
            return false;
        }

        if (this == o) {
            return true;
        }

        ControllerTile otherController = (ControllerTile) o;

        if (world.getDimension().getType() != otherController.world.getDimension().getType()) {
            return false;
        }

        return pos.equals(otherController.pos);
    }

    @Override
    public int hashCode() {
        int result = pos.hashCode();
        result = 31 * result + world.getDimension().getType().getId();

        return result;
    }
}
