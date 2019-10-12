package com.raoulvdberge.refinedstorage.apiimpl.network.node.diskdrive;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import com.raoulvdberge.refinedstorage.api.storage.IStorageProvider;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskContainerContext;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskProvider;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.storage.cache.FluidStorageCache;
import com.raoulvdberge.refinedstorage.apiimpl.storage.cache.ItemStorageCache;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventory;
import com.raoulvdberge.refinedstorage.inventory.item.BaseItemHandler;
import com.raoulvdberge.refinedstorage.inventory.listener.NetworkNodeListener;
import com.raoulvdberge.refinedstorage.tile.DiskDriveTile;
import com.raoulvdberge.refinedstorage.tile.config.*;
import com.raoulvdberge.refinedstorage.util.AccessTypeUtils;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.List;
import java.util.function.Predicate;

public class DiskDriveNetworkNode extends NetworkNode implements IStorageProvider, IComparable, IWhitelistBlacklist, IPrioritizable, IType, IAccessType, IStorageDiskContainerContext {
    public enum DiskState {
        NONE,
        NORMAL,
        DISCONNECTED,
        NEAR_CAPACITY,
        FULL;

        public static final int DISK_NEAR_CAPACITY_THRESHOLD = 75;

        public static DiskState get(int stored, int capacity) {
            if (stored == capacity) {
                return FULL;
            } else if ((int) ((float) stored / (float) capacity * 100F) >= DISK_NEAR_CAPACITY_THRESHOLD) {
                return NEAR_CAPACITY;
            } else {
                return NORMAL;
            }
        }
    }

    public static final Predicate<ItemStack> VALIDATOR_STORAGE_DISK = s -> s.getItem() instanceof IStorageDiskProvider && ((IStorageDiskProvider) s.getItem()).isValid(s);

    public static final ResourceLocation ID = new ResourceLocation(RS.ID, "disk_drive");

    private static final String NBT_PRIORITY = "Priority";
    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_FLUID_FILTERS = "FluidFilters";

    private static final int DISK_STATE_UPDATE_THROTTLE = 30;

    private int ticksSinceBlockUpdateRequested;
    private boolean blockUpdateRequested;

    private BaseItemHandler disks = new BaseItemHandler(8, new NetworkNodeListener(this), VALIDATOR_STORAGE_DISK) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (!world.isRemote) {
                StackUtils.createStorages(
                    (ServerWorld) world,
                    getStackInSlot(slot),
                    slot,
                    itemDisks,
                    fluidDisks,
                    s -> new StorageDiskItemDriveWrapper(DiskDriveNetworkNode.this, s),
                    s -> new StorageDiskFluidDriveWrapper(DiskDriveNetworkNode.this, s)
                );

                if (network != null) {
                    network.getItemStorageCache().invalidate();
                    network.getFluidStorageCache().invalidate();
                }

                WorldUtils.updateBlock(world, pos);
            }
        }
    };

    private BaseItemHandler itemFilters = new BaseItemHandler(9, new NetworkNodeListener(this));
    private FluidInventory fluidFilters = new FluidInventory(9, new NetworkNodeListener(this));

    private IStorageDisk[] itemDisks = new IStorageDisk[8];
    private IStorageDisk[] fluidDisks = new IStorageDisk[8];

    private AccessType accessType = AccessType.INSERT_EXTRACT;
    private int priority = 0;
    private int compare = IComparer.COMPARE_NBT;
    private int mode = IWhitelistBlacklist.BLACKLIST;
    private int type = IType.ITEMS;

    public DiskDriveNetworkNode(World world, BlockPos pos) {
        super(world, pos);
    }

    public IStorageDisk[] getItemDisks() {
        return itemDisks;
    }

    public IStorageDisk[] getFluidDisks() {
        return fluidDisks;
    }

    @Override
    public int getEnergyUsage() {
        int usage = RS.SERVER_CONFIG.getDiskDrive().getUsage();

        for (IStorage storage : itemDisks) {
            if (storage != null) {
                usage += RS.SERVER_CONFIG.getDiskDrive().getDiskUsage();
            }
        }
        for (IStorage storage : fluidDisks) {
            if (storage != null) {
                usage += RS.SERVER_CONFIG.getDiskDrive().getDiskUsage();
            }
        }

        return usage;
    }

    @Override
    public void update() {
        super.update();

        if (blockUpdateRequested) {
            ++ticksSinceBlockUpdateRequested;

            if (ticksSinceBlockUpdateRequested > DISK_STATE_UPDATE_THROTTLE) {
                WorldUtils.updateBlock(world, pos);

                this.blockUpdateRequested = false;
                this.ticksSinceBlockUpdateRequested = 0;
            }
        } else {
            this.ticksSinceBlockUpdateRequested = 0;
        }
    }

    void requestBlockUpdate() {
        this.blockUpdateRequested = true;
    }

    @Override
    public void onConnectedStateChange(INetwork network, boolean state) {
        super.onConnectedStateChange(network, state);

        network.getNodeGraph().runActionWhenPossible(ItemStorageCache.INVALIDATE);
        network.getNodeGraph().runActionWhenPossible(FluidStorageCache.INVALIDATE);

        WorldUtils.updateBlock(world, pos);
    }

    @Override
    public void addItemStorages(List<IStorage<ItemStack>> storages) {
        for (IStorage<ItemStack> storage : this.itemDisks) {
            if (storage != null) {
                storages.add(storage);
            }
        }
    }

    @Override
    public void addFluidStorages(List<IStorage<FluidStack>> storages) {
        for (IStorage<FluidStack> storage : this.fluidDisks) {
            if (storage != null) {
                storages.add(storage);
            }
        }
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);

        StackUtils.readItems(disks, 0, tag);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);

        StackUtils.writeItems(disks, 0, tag);

        return tag;
    }

    @Override
    public CompoundNBT writeConfiguration(CompoundNBT tag) {
        super.writeConfiguration(tag);

        StackUtils.writeItems(itemFilters, 1, tag);

        tag.put(NBT_FLUID_FILTERS, fluidFilters.writeToNbt());
        tag.putInt(NBT_PRIORITY, priority);
        tag.putInt(NBT_COMPARE, compare);
        tag.putInt(NBT_MODE, mode);
        tag.putInt(NBT_TYPE, type);

        AccessTypeUtils.writeAccessType(tag, accessType);

        return tag;
    }

    @Override
    public void readConfiguration(CompoundNBT tag) {
        super.readConfiguration(tag);

        StackUtils.readItems(itemFilters, 1, tag);

        if (tag.contains(NBT_FLUID_FILTERS)) {
            fluidFilters.readFromNbt(tag.getCompound(NBT_FLUID_FILTERS));
        }

        if (tag.contains(NBT_PRIORITY)) {
            priority = tag.getInt(NBT_PRIORITY);
        }

        if (tag.contains(NBT_COMPARE)) {
            compare = tag.getInt(NBT_COMPARE);
        }

        if (tag.contains(NBT_MODE)) {
            mode = tag.getInt(NBT_MODE);
        }

        if (tag.contains(NBT_TYPE)) {
            type = tag.getInt(NBT_TYPE);
        }

        accessType = AccessTypeUtils.readAccessType(tag);
    }

    @Override
    public int getCompare() {
        return compare;
    }

    @Override
    public void setCompare(int compare) {
        this.compare = compare;

        markDirty();
    }

    @Override
    public int getWhitelistBlacklistMode() {
        return mode;
    }

    @Override
    public void setWhitelistBlacklistMode(int mode) {
        this.mode = mode;

        markDirty();
    }

    @Override
    public AccessType getAccessType() {
        return accessType;
    }

    @Override
    public void setAccessType(AccessType value) {
        this.accessType = value;

        if (network != null) {
            network.getFluidStorageCache().invalidate();
            network.getItemStorageCache().invalidate();
        }

        markDirty();
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;

        markDirty();

        if (network != null) {
            network.getItemStorageCache().sort();
            network.getFluidStorageCache().sort();
        }
    }

    public DiskState[] getDiskState() {
        DiskState[] diskStates = new DiskState[8];

        for (int i = 0; i < 8; ++i) {
            DiskState state = DiskState.NONE;

            if (itemDisks[i] != null || fluidDisks[i] != null) {
                if (!canUpdate()) {
                    state = DiskState.DISCONNECTED;
                } else {
                    state = DiskState.get(
                        itemDisks[i] != null ? itemDisks[i].getStored() : fluidDisks[i].getStored(),
                        itemDisks[i] != null ? itemDisks[i].getCapacity() : fluidDisks[i].getCapacity()
                    );
                }
            }

            diskStates[i] = state;
        }

        return diskStates;
    }

    public IItemHandler getDisks() {
        return disks;
    }

    @Override
    public int getType() {
        return world.isRemote ? DiskDriveTile.TYPE.getValue() : type;
    }

    @Override
    public void setType(int type) {
        this.type = type;

        markDirty();
    }

    @Override
    public IItemHandlerModifiable getItemFilters() {
        return itemFilters;
    }

    @Override
    public FluidInventory getFluidFilters() {
        return fluidFilters;
    }

    @Override
    public IItemHandler getDrops() {
        return disks;
    }
}
