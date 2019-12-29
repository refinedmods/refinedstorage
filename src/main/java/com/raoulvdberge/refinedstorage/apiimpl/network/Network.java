package com.raoulvdberge.refinedstorage.apiimpl.network;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingManager;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.network.INetworkNodeGraph;
import com.raoulvdberge.refinedstorage.api.network.NetworkType;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IFluidGridHandler;
import com.raoulvdberge.refinedstorage.api.network.grid.handler.IItemGridHandler;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemManager;
import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;
import com.raoulvdberge.refinedstorage.api.network.security.ISecurityManager;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.storage.cache.IStorageCache;
import com.raoulvdberge.refinedstorage.api.storage.externalstorage.IExternalStorage;
import com.raoulvdberge.refinedstorage.api.storage.tracker.IStorageTracker;
import com.raoulvdberge.refinedstorage.api.util.Action;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.CraftingManager;
import com.raoulvdberge.refinedstorage.apiimpl.network.grid.handler.FluidGridHandler;
import com.raoulvdberge.refinedstorage.apiimpl.network.grid.handler.ItemGridHandler;
import com.raoulvdberge.refinedstorage.apiimpl.network.item.NetworkItemManager;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.RootNetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.network.security.SecurityManager;
import com.raoulvdberge.refinedstorage.apiimpl.storage.cache.FluidStorageCache;
import com.raoulvdberge.refinedstorage.apiimpl.storage.cache.ItemStorageCache;
import com.raoulvdberge.refinedstorage.apiimpl.storage.tracker.FluidStorageTracker;
import com.raoulvdberge.refinedstorage.apiimpl.storage.tracker.ItemStorageTracker;
import com.raoulvdberge.refinedstorage.block.ControllerBlock;
import com.raoulvdberge.refinedstorage.energy.BaseEnergyStorage;
import com.raoulvdberge.refinedstorage.tile.ControllerTile;
import com.raoulvdberge.refinedstorage.tile.config.IRedstoneConfigurable;
import com.raoulvdberge.refinedstorage.tile.config.RedstoneMode;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class Network implements INetwork, IRedstoneConfigurable {
    private static final int THROTTLE_INACTIVE_TO_ACTIVE = 20;
    private static final int THROTTLE_ACTIVE_TO_INACTIVE = 4;

    private static final String NBT_ENERGY = "Energy";
    private static final String NBT_ITEM_STORAGE_TRACKER = "ItemStorageTracker";
    private static final String NBT_FLUID_STORAGE_TRACKER = "FluidStorageTracker";

    private final IItemGridHandler itemGridHandler = new ItemGridHandler(this);
    private final IFluidGridHandler fluidGridHandler = new FluidGridHandler(this);
    private final INetworkItemManager networkItemManager = new NetworkItemManager(this);
    private final INetworkNodeGraph nodeGraph = new NetworkNodeGraph(this);
    private final ICraftingManager craftingManager = new CraftingManager(this);
    private final ISecurityManager securityManager = new SecurityManager(this);
    private final IStorageCache<ItemStack> itemStorage = new ItemStorageCache(this);
    private final ItemStorageTracker itemStorageTracker = new ItemStorageTracker(this::markDirty);
    private final IStorageCache<FluidStack> fluidStorage = new FluidStorageCache(this);
    private final FluidStorageTracker fluidStorageTracker = new FluidStorageTracker(this::markDirty);
    private final BaseEnergyStorage energy = new BaseEnergyStorage(RS.SERVER_CONFIG.getController().getCapacity(), RS.SERVER_CONFIG.getController().getMaxTransfer(), 0);
    private final RootNetworkNode root;

    private final BlockPos pos;
    private final World world;
    private final NetworkType type;
    private ControllerBlock.EnergyType lastEnergyType = ControllerBlock.EnergyType.OFF;
    private RedstoneMode redstoneMode = RedstoneMode.IGNORE;

    private boolean throttlingDisabled = true; // Will be enabled after first update
    private boolean couldRun;
    private int ticksSinceUpdateChanged;

    public Network(World world, BlockPos pos, NetworkType type) {
        this.pos = pos;
        this.world = world;
        this.type = type;
        this.root = new RootNetworkNode(this, world, pos);
        this.nodeGraph.addListener(() -> {
            TileEntity tile = world.getTileEntity(pos);

            if (tile instanceof ControllerTile) {
                ((ControllerTile) tile).getDataManager().sendParameterToWatchers(ControllerTile.NODES);
            }
        });
    }

    public RootNetworkNode getRoot() {
        return root;
    }

    public BaseEnergyStorage getEnergy() {
        return energy;
    }

    @Override
    public BlockPos getPosition() {
        return pos;
    }

    @Override
    public boolean canRun() {
        return this.energy.getEnergyStored() >= getEnergyUsage() && redstoneMode.isEnabled(world, pos);
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

                if (!craftingManager.getTasks().isEmpty()) {
                    markDirty();
                }
            }

            if (type == NetworkType.NORMAL) {
                if (!RS.SERVER_CONFIG.getController().getUseEnergy()) {
                    this.energy.setStored(this.energy.getMaxEnergyStored());
                } else {
                    this.energy.extractEnergyBypassCanExtract(getEnergyUsage(), false);
                }
            } else if (type == NetworkType.CREATIVE) {
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

                BlockState state = world.getBlockState(pos);
                if (state.getBlock() instanceof ControllerBlock) {
                    world.setBlockState(pos, state.with(ControllerBlock.ENERGY_TYPE, energyType));
                }
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
    public INetworkItemManager getNetworkItemManager() {
        return networkItemManager;
    }

    @Override
    public void onRemoved() {
        nodeGraph.disconnectAll();
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
    public World getWorld() {
        return world;
    }

    @Override
    public INetwork readFromNbt(CompoundNBT tag) {
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

        return this;
    }

    @Override
    public CompoundNBT writeToNbt(CompoundNBT tag) {
        tag.putInt(NBT_ENERGY, this.energy.getEnergyStored());

        redstoneMode.write(tag);

        craftingManager.writeToNbt(tag);

        tag.put(NBT_ITEM_STORAGE_TRACKER, itemStorageTracker.serializeNbt());
        tag.put(NBT_FLUID_STORAGE_TRACKER, fluidStorageTracker.serializeNbt());

        return tag;
    }

    @Override
    public void markDirty() {
        API.instance().getNetworkManager((ServerWorld) world).markForSaving();
    }

    public static int getEnergyScaled(int stored, int capacity, int scale) {
        return (int) ((float) stored / (float) capacity * (float) scale);
    }

    public ControllerBlock.EnergyType getEnergyType() {
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
            if (node.isActive()) {
                usage += node.getEnergyUsage();
            }
        }

        return usage;
    }

    @Override
    public IEnergyStorage getEnergyStorage() {
        return energy;
    }

    @Override
    public NetworkType getType() {
        return type;
    }
}
