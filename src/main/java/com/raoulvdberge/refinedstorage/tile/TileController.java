package com.raoulvdberge.refinedstorage.tile;

import com.google.common.base.Preconditions;
import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingManager;
import com.raoulvdberge.refinedstorage.api.energy.EnergyProxy;
import com.raoulvdberge.refinedstorage.api.energy.IEnergy;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.INetworkNodeGraph;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IFluidGridHandler;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IItemGridHandler;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemHandler;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNodeProxy;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterListener;
import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReaderWriterManager;
import com.raoulvdberge.refinedstorage.api.network.security.ISecurityManager;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.storage.IStorageCache;
import com.raoulvdberge.refinedstorage.api.storage.IStorageTracker;
import com.raoulvdberge.refinedstorage.api.storage.externalstorage.IStorageExternal;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.CraftingManager;
import com.raoulvdberge.refinedstorage.apiimpl.energy.Energy;
import com.raoulvdberge.refinedstorage.apiimpl.network.NetworkNodeGraph;
import com.raoulvdberge.refinedstorage.apiimpl.network.grid.handler.FluidGridHandler;
import com.raoulvdberge.refinedstorage.apiimpl.network.grid.handler.ItemGridHandler;
import com.raoulvdberge.refinedstorage.apiimpl.network.item.NetworkItemHandler;
import com.raoulvdberge.refinedstorage.apiimpl.network.readerwriter.ReaderWriterManager;
import com.raoulvdberge.refinedstorage.apiimpl.network.security.SecurityManager;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageCacheFluid;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageCacheItem;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageTrackerFluid;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageTrackerItem;
import com.raoulvdberge.refinedstorage.block.BlockController;
import com.raoulvdberge.refinedstorage.block.ControllerEnergyType;
import com.raoulvdberge.refinedstorage.block.ControllerType;
import com.raoulvdberge.refinedstorage.capability.CapabilityNetworkNodeProxy;
import com.raoulvdberge.refinedstorage.tile.config.IRedstoneConfigurable;
import com.raoulvdberge.refinedstorage.tile.config.RedstoneMode;
import com.raoulvdberge.refinedstorage.tile.data.RSSerializers;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
    public static final TileDataParameter<Integer, TileController> ENERGY_STORED = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> t.getEnergy().getStored());
    public static final TileDataParameter<Integer, TileController> ENERGY_CAPACITY = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> t.getEnergy().getCapacity());
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
    public static final String NBT_ENERGY_TYPE = "EnergyType";

    private static final String NBT_ITEM_STORAGE_TRACKER = "ItemStorageTracker";
    private static final String NBT_FLUID_STORAGE_TRACKER = "FluidStorageTracker";

    private IItemGridHandler itemGridHandler = new ItemGridHandler(this);
    private IFluidGridHandler fluidGridHandler = new FluidGridHandler(this);

    private INetworkItemHandler networkItemHandler = new NetworkItemHandler(this);

    private INetworkNodeGraph nodeGraph = new NetworkNodeGraph(this);

    private ICraftingManager craftingManager = new CraftingManager(this);

    private ISecurityManager securityManager = new SecurityManager(this);

    private IStorageCache<ItemStack> itemStorage = new StorageCacheItem(this);
    private StorageTrackerItem itemStorageTracker = new StorageTrackerItem(this::markDirty);

    private IStorageCache<FluidStack> fluidStorage = new StorageCacheFluid(this);
    private StorageTrackerFluid fluidStorageTracker = new StorageTrackerFluid(this::markDirty);

    private IReaderWriterManager readerWriterManager = new ReaderWriterManager(this);

    private final IEnergy energy = new Energy(RS.INSTANCE.config.controllerCapacity);
    private final EnergyProxy energyProxy = new EnergyProxy(this.energy, RS.INSTANCE.config.controllerMaxReceive, 0);

    private boolean throttlingDisabled = true; // Will be enabled after first update
    private boolean couldRun;
    private int ticksSinceUpdateChanged;

    private ControllerType type;
    private ControllerEnergyType energyType = ControllerEnergyType.OFF;

    private RedstoneMode redstoneMode = RedstoneMode.IGNORE;

    public TileController() {
        dataManager.addWatchedParameter(REDSTONE_MODE);
        dataManager.addWatchedParameter(ENERGY_USAGE);
        dataManager.addWatchedParameter(ENERGY_STORED);
        dataManager.addParameter(ENERGY_CAPACITY);
        dataManager.addParameter(NODES);

        readerWriterManager.addListener(new IReaderWriterListener() {
            @Override
            public void onAttached() {
            }

            @Override
            public void onChanged() {
                markDirty();
            }
        });
    }
    
    @Override
    public IEnergy getEnergy() {
        return this.energy;
    }

    @Override
    public BlockPos getPosition() {
        return pos;
    }

    @Override
    public boolean canRun() {
        return this.energy.getStored() > 0 && redstoneMode.isEnabled(world, pos);
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

                readerWriterManager.update();

                if (!craftingManager.getTasks().isEmpty()) {
                    markDirty();
                }
            }

            if (getType() == ControllerType.NORMAL) {
                if (!RS.INSTANCE.config.controllerUsesEnergy) {
                    this.energy.setStored(this.energy.getCapacity());
                } else if (this.energy.extract(getEnergyUsage(), true) >= 0) {
                    this.energy.extract(getEnergyUsage(), false);
                } else {
                    this.energy.setStored(0);
                }
            } else if (getType() == ControllerType.CREATIVE) {
            	this.energy.setStored(this.energy.getCapacity());
            }

            boolean canRun = canRun();

            if (couldRun != canRun) {
                ++ticksSinceUpdateChanged;

                if ((canRun ? (ticksSinceUpdateChanged > THROTTLE_INACTIVE_TO_ACTIVE) : (ticksSinceUpdateChanged > THROTTLE_ACTIVE_TO_INACTIVE)) || throttlingDisabled) {
                    ticksSinceUpdateChanged = 0;
                    couldRun = canRun;
                    throttlingDisabled = false;

                    nodeGraph.rebuild();
                    securityManager.rebuild();
                }
            } else {
                ticksSinceUpdateChanged = 0;
            }

            ControllerEnergyType energyType = getEnergyType();

            if (this.energyType != energyType) {
                this.energyType = energyType;

                WorldUtils.updateBlock(world, pos);
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
    public IReaderWriterManager getReaderWriterManager() {
        return readerWriterManager;
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
                if (storage instanceof IStorageExternal && !simulate) {
                    ((IStorageExternal) storage).update(this);

                    insertedExternally += size;
                }

                break;
            } else {
                // The external storage is responsible for sending changes, we don't need to anymore
                if (size != remainder.getCount() && storage instanceof IStorageExternal && !simulate) {
                    ((IStorageExternal) storage).update(this);

                    insertedExternally += size - remainder.getCount();
                }

                size = remainder.getCount();
            }
        }

        if (!simulate && inserted - insertedExternally > 0) {
            itemStorage.add(stack, inserted - insertedExternally, false, false);
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
                if (storage instanceof IStorageExternal && !simulate) {
                    ((IStorageExternal) storage).update(this);

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
            itemStorage.remove(newStack, newStack.getCount() - extractedExternally, false);
        }

        return newStack;
    }


    @Override
    public FluidStack insertFluid(@Nonnull FluidStack stack, int size, boolean simulate) {
        if (stack == null || fluidStorage.getStorages().isEmpty()) {
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

            remainder = storage.insert(remainder, size, simulate);

            if (!simulate) {
                inserted += storage.getCacheDelta(storedPre, size, remainder);
            }

            if (remainder == null) {
                // The external storage is responsible for sending changes, we don't need to anymore
                if (storage instanceof IStorageExternal && !simulate) {
                    ((IStorageExternal) storage).update(this);

                    insertedExternally += size;
                }

                break;
            } else {
                // The external storage is responsible for sending changes, we don't need to anymore
                if (size != remainder.amount && storage instanceof IStorageExternal && !simulate) {
                    ((IStorageExternal) storage).update(this);

                    insertedExternally += size - remainder.amount;
                }

                size = remainder.amount;
            }
        }

        if (!simulate && inserted - insertedExternally > 0) {
            fluidStorage.add(stack, inserted - insertedExternally, false, false);
        }

        return remainder;
    }

    @Override
    public FluidStack extractFluid(@Nonnull FluidStack stack, int size, int flags, boolean simulate) {
        int requested = size;
        int received = 0;

        int extractedExternally = 0;

        FluidStack newStack = null;

        for (IStorage<FluidStack> storage : this.fluidStorage.getStorages()) {
            FluidStack took = null;

            if (storage.getAccessType() != AccessType.INSERT) {
                took = storage.extract(stack, requested - received, flags, simulate);
            }

            if (took != null) {
                // The external storage is responsible for sending changes, we don't need to anymore
                if (storage instanceof IStorageExternal && !simulate) {
                    ((IStorageExternal) storage).update(this);

                    extractedExternally += took.amount;
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

        if (newStack != null && newStack.amount - extractedExternally > 0 && !simulate) {
            fluidStorage.remove(newStack, newStack.amount - extractedExternally, false);
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
    public void read(NBTTagCompound tag) {
        super.read(tag);

        if (tag.hasKey(NBT_ENERGY)) {
            this.energy.setStored(tag.getInteger(NBT_ENERGY));
        }

        redstoneMode = RedstoneMode.read(tag);

        craftingManager.readFromNbt(tag);

        readerWriterManager.readFromNbt(tag);

        if (tag.hasKey(NBT_ITEM_STORAGE_TRACKER)) {
            itemStorageTracker.readFromNBT(tag.getTagList(NBT_ITEM_STORAGE_TRACKER, Constants.NBT.TAG_COMPOUND));
        }

        if (tag.hasKey(NBT_FLUID_STORAGE_TRACKER)) {
            fluidStorageTracker.readFromNBT(tag.getTagList(NBT_FLUID_STORAGE_TRACKER, Constants.NBT.TAG_COMPOUND));
        }
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        tag.setInteger(NBT_ENERGY, this.energy.getStored());

        redstoneMode.write(tag);

        craftingManager.writeToNBT(tag);

        readerWriterManager.writeToNbt(tag);

        tag.setTag(NBT_ITEM_STORAGE_TRACKER, itemStorageTracker.serializeNBT());
        tag.setTag(NBT_FLUID_STORAGE_TRACKER, fluidStorageTracker.serializeNBT());

        return tag;
    }

    @Override
    public NBTTagCompound writeUpdate(NBTTagCompound tag) {
        super.writeUpdate(tag);

        tag.setInteger(NBT_ENERGY_TYPE, getEnergyType().getId());

        return tag;
    }

    @Override
    public void readUpdate(NBTTagCompound tag) {
        if (tag.hasKey(NBT_ENERGY_TYPE)) {
            this.energyType = ControllerEnergyType.getById(tag.getInteger(NBT_ENERGY_TYPE));
        }

        super.readUpdate(tag);
    }

    public static int getEnergyScaled(int stored, int capacity, int scale) {
        return (int) ((float) stored / (float) capacity * (float) scale);
    }

    public static ControllerEnergyType getEnergyType(int stored, int capacity) {
        int energy = getEnergyScaled(stored, capacity, 100);

        if (energy <= 0) {
            return ControllerEnergyType.OFF;
        } else if (energy <= 10) {
            return ControllerEnergyType.NEARLY_OFF;
        } else if (energy <= 20) {
            return ControllerEnergyType.NEARLY_ON;
        }

        return ControllerEnergyType.ON;
    }

    public ControllerEnergyType getEnergyType() {
        if (world.isRemote) {
            return energyType;
        }

        return getEnergyType(this.energy.getStored(), this.energy.getCapacity());
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
        usage += nodeGraph.all().stream().mapToInt(x-> x.getEnergyUsage()).sum();
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
            return CapabilityEnergy.ENERGY.cast(energyProxy);
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
