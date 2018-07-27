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
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.IGuiStorage;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageCacheFluid;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageCacheItem;
import com.raoulvdberge.refinedstorage.apiimpl.util.OneSixMigrationHelper;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventory;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.inventory.listener.ListenerNetworkNode;
import com.raoulvdberge.refinedstorage.tile.TileDiskDrive;
import com.raoulvdberge.refinedstorage.tile.config.*;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import com.raoulvdberge.refinedstorage.util.AccessTypeUtils;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import com.raoulvdberge.refinedstorage.util.WorldUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.List;
import java.util.function.Predicate;

public class NetworkNodeDiskDrive extends NetworkNode implements IGuiStorage, IStorageProvider, IComparable, IFilterable, IPrioritizable, IType, IAccessType, IStorageDiskContainerContext {
    public static final Predicate<ItemStack> VALIDATOR_STORAGE_DISK = s -> s.getItem() instanceof IStorageDiskProvider && ((IStorageDiskProvider) s.getItem()).isValid(s);

    public static final String ID = "disk_drive";

    private static final String NBT_PRIORITY = "Priority";
    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_FLUID_FILTERS = "FluidFilters";

    private static final int DISK_STATE_UPDATE_THROTTLE = 30;

    private int ticksSinceBlockUpdateRequested;
    private boolean blockUpdateRequested;

    private ItemHandlerBase disks = new ItemHandlerBase(8, new ListenerNetworkNode(this), VALIDATOR_STORAGE_DISK) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
                StackUtils.createStorages(
                    world,
                    getStackInSlot(slot),
                    slot,
                    itemDisks,
                    fluidDisks,
                    s -> new StorageDiskItemDriveWrapper(NetworkNodeDiskDrive.this, s),
                    s -> new StorageDiskFluidDriveWrapper(NetworkNodeDiskDrive.this, s)
                );

                if (network != null) {
                    network.getItemStorageCache().invalidate();
                    network.getFluidStorageCache().invalidate();
                }

                WorldUtils.updateBlock(world, pos);
            }
        }
    };

    private ItemHandlerBase itemFilters = new ItemHandlerBase(9, new ListenerNetworkNode(this));
    private FluidInventory fluidFilters = new FluidInventory(9, new ListenerNetworkNode(this));

    private IStorageDisk[] itemDisks = new IStorageDisk[8];
    private IStorageDisk[] fluidDisks = new IStorageDisk[8];

    private AccessType accessType = AccessType.INSERT_EXTRACT;
    private int priority = 0;
    private int compare = IComparer.COMPARE_NBT | IComparer.COMPARE_DAMAGE;
    private int mode = IFilterable.BLACKLIST;
    private int type = IType.ITEMS;

    public NetworkNodeDiskDrive(World world, BlockPos pos) {
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
        int usage = RS.INSTANCE.config.diskDriveUsage;

        for (IStorage storage : itemDisks) {
            if (storage != null) {
                usage += RS.INSTANCE.config.diskDrivePerDiskUsage;
            }
        }
        for (IStorage storage : fluidDisks) {
            if (storage != null) {
                usage += RS.INSTANCE.config.diskDrivePerDiskUsage;
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

        network.getNodeGraph().addPostRebuildHandler(StorageCacheItem.INVALIDATE);
        network.getNodeGraph().addPostRebuildHandler(StorageCacheFluid.INVALIDATE);

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
    public void read(NBTTagCompound tag) {
        super.read(tag);

        StackUtils.readItems(disks, 0, tag);

        if (API.instance().getOneSixMigrationHelper().migrateDiskInventory(world, disks)) {
            markDirty();
        }
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        StackUtils.writeItems(disks, 0, tag);

        return tag;
    }

    @Override
    public NBTTagCompound writeConfiguration(NBTTagCompound tag) {
        super.writeConfiguration(tag);

        StackUtils.writeItems(itemFilters, 1, tag);

        tag.setTag(NBT_FLUID_FILTERS, fluidFilters.writeToNbt());
        tag.setInteger(NBT_PRIORITY, priority);
        tag.setInteger(NBT_COMPARE, compare);
        tag.setInteger(NBT_MODE, mode);
        tag.setInteger(NBT_TYPE, type);

        AccessTypeUtils.writeAccessType(tag, accessType);

        return tag;
    }

    @Override
    public void readConfiguration(NBTTagCompound tag) {
        super.readConfiguration(tag);

        StackUtils.readItems(itemFilters, 1, tag);

        if (tag.hasKey(NBT_FLUID_FILTERS)) {
            fluidFilters.readFromNbt(tag.getCompoundTag(NBT_FLUID_FILTERS));
        }

        if (tag.hasKey(NBT_PRIORITY)) {
            priority = tag.getInteger(NBT_PRIORITY);
        }

        if (tag.hasKey(NBT_COMPARE)) {
            compare = tag.getInteger(NBT_COMPARE);
        }

        if (tag.hasKey(NBT_MODE)) {
            mode = tag.getInteger(NBT_MODE);
        }

        if (tag.hasKey(NBT_TYPE)) {
            type = tag.getInteger(NBT_TYPE);
        }

        accessType = AccessTypeUtils.readAccessType(tag);

        OneSixMigrationHelper.migrateEmptyWhitelistToEmptyBlacklist(version, this, itemFilters);
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
    public int getMode() {
        return mode;
    }

    @Override
    public void setMode(int mode) {
        this.mode = mode;

        markDirty();
    }

    @Override
    public String getGuiTitle() {
        return "block.refinedstorage:disk_drive.name";
    }

    @Override
    public TileDataParameter<Integer, ?> getTypeParameter() {
        return TileDiskDrive.TYPE;
    }

    @Override
    public TileDataParameter<Integer, ?> getRedstoneModeParameter() {
        return TileDiskDrive.REDSTONE_MODE;
    }

    @Override
    public TileDataParameter<Integer, ?> getCompareParameter() {
        return TileDiskDrive.COMPARE;
    }

    @Override
    public TileDataParameter<Integer, ?> getFilterParameter() {
        return TileDiskDrive.MODE;
    }

    @Override
    public TileDataParameter<Integer, ?> getPriorityParameter() {
        return TileDiskDrive.PRIORITY;
    }

    @Override
    public TileDataParameter<AccessType, ?> getAccessTypeParameter() {
        return TileDiskDrive.ACCESS_TYPE;
    }

    @Override
    public int getStored() {
        return TileDiskDrive.STORED.getValue();
    }

    @Override
    public int getCapacity() {
        return TileDiskDrive.CAPACITY.getValue();
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

    public IItemHandler getDisks() {
        return disks;
    }

    @Override
    public int getType() {
        return world.isRemote ? TileDiskDrive.TYPE.getValue() : type;
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
