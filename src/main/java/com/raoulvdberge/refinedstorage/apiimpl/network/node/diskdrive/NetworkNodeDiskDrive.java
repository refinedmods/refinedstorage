package com.raoulvdberge.refinedstorage.apiimpl.network.node.diskdrive;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.api.storage.*;
import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.IGuiStorage;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageCacheFluid;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageCacheItem;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerBase;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerFluid;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerListenerNetworkNode;
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

import java.util.List;
import java.util.function.Predicate;

public class NetworkNodeDiskDrive extends NetworkNode implements IGuiStorage, IStorageProvider, IComparable, IFilterable, IPrioritizable, IType, IExcessVoidable, IAccessType {
    public static final Predicate<ItemStack> VALIDATOR_STORAGE_DISK = s -> s.getItem() instanceof IStorageDiskProvider && ((IStorageDiskProvider) s.getItem()).create(s).isValid(s);

    public static final String ID = "disk_drive";

    private static final String NBT_PRIORITY = "Priority";
    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_VOID_EXCESS = "VoidExcess";

    private ItemHandlerBase disks = new ItemHandlerBase(8, new ItemHandlerListenerNetworkNode(this), VALIDATOR_STORAGE_DISK) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);

            if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
                StackUtils.createStorages(
                    getStackInSlot(slot),
                    slot,
                    itemStorages,
                    fluidStorages,
                    s -> new StorageItemDiskDrive(NetworkNodeDiskDrive.this, s),
                    s -> new StorageFluidDiskDrive(NetworkNodeDiskDrive.this, s)
                );

                if (network != null) {
                    network.getItemStorageCache().invalidate();
                    network.getFluidStorageCache().invalidate();
                }

                WorldUtils.updateBlock(world, pos);
            }
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (itemStorages[slot] != null) {
                itemStorages[slot].writeToNBT();
            }

            if (fluidStorages[slot] != null) {
                fluidStorages[slot].writeToNBT();
            }

            return super.extractItem(slot, amount, simulate);
        }
    };

    private ItemHandlerBase itemFilters = new ItemHandlerBase(9, new ItemHandlerListenerNetworkNode(this));
    private ItemHandlerFluid fluidFilters = new ItemHandlerFluid(9, new ItemHandlerListenerNetworkNode(this));

    private IStorageDisk[] itemStorages = new IStorageDisk[8];
    private IStorageDisk[] fluidStorages = new IStorageDisk[8];

    private AccessType accessType = AccessType.INSERT_EXTRACT;
    private int priority = 0;
    private int compare = IComparer.COMPARE_NBT | IComparer.COMPARE_DAMAGE;
    private int mode = IFilterable.WHITELIST;
    private int type = IType.ITEMS;
    private boolean voidExcess = false;

    public NetworkNodeDiskDrive(World world, BlockPos pos) {
        super(world, pos);
    }

    public IStorageDisk[] getItemStorages() {
        return itemStorages;
    }

    public IStorageDisk[] getFluidStorages() {
        return fluidStorages;
    }

    @Override
    public int getEnergyUsage() {
        int usage = RS.INSTANCE.config.diskDriveUsage;

        for (IStorage storage : itemStorages) {
            if (storage != null) {
                usage += RS.INSTANCE.config.diskDrivePerDiskUsage;
            }
        }
        for (IStorage storage : fluidStorages) {
            if (storage != null) {
                usage += RS.INSTANCE.config.diskDrivePerDiskUsage;
            }
        }

        return usage;
    }

    public void onBreak() {
        for (IStorageDisk storage : this.itemStorages) {
            if (storage != null) {
                storage.writeToNBT();
            }
        }

        for (IStorageDisk storage : this.fluidStorages) {
            if (storage != null) {
                storage.writeToNBT();
            }
        }
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
        for (IStorage<ItemStack> storage : this.itemStorages) {
            if (storage != null) {
                storages.add(storage);
            }
        }
    }

    @Override
    public void addFluidStorages(List<IStorage<FluidStack>> storages) {
        for (IStorage<FluidStack> storage : this.fluidStorages) {
            if (storage != null) {
                storages.add(storage);
            }
        }
    }

    @Override
    public void read(NBTTagCompound tag) {
        super.read(tag);

        StackUtils.readItems(disks, 0, tag);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public NBTTagCompound write(NBTTagCompound tag) {
        super.write(tag);

        for (int i = 0; i < disks.getSlots(); ++i) {
            if (itemStorages[i] != null) {
                itemStorages[i].writeToNBT();
            }

            if (fluidStorages[i] != null) {
                fluidStorages[i].writeToNBT();
            }
        }

        StackUtils.writeItems(disks, 0, tag);

        return tag;
    }

    @Override
    public NBTTagCompound writeConfiguration(NBTTagCompound tag) {
        super.writeConfiguration(tag);

        StackUtils.writeItems(itemFilters, 1, tag);
        StackUtils.writeItems(fluidFilters, 2, tag);

        tag.setInteger(NBT_PRIORITY, priority);
        tag.setInteger(NBT_COMPARE, compare);
        tag.setInteger(NBT_MODE, mode);
        tag.setInteger(NBT_TYPE, type);
        tag.setBoolean(NBT_VOID_EXCESS, voidExcess);

        AccessTypeUtils.writeAccessType(tag, accessType);

        return tag;
    }

    @Override
    public void readConfiguration(NBTTagCompound tag) {
        super.readConfiguration(tag);

        StackUtils.readItems(itemFilters, 1, tag);
        StackUtils.readItems(fluidFilters, 2, tag);

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

        if (tag.hasKey(NBT_VOID_EXCESS)) {
            voidExcess = tag.getBoolean(NBT_VOID_EXCESS);
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
    public TileDataParameter<Boolean, ?> getVoidExcessParameter() {
        return TileDiskDrive.VOID_EXCESS;
    }

    @Override
    public TileDataParameter<AccessType, ?> getAccessTypeParameter() {
        return TileDiskDrive.ACCESS_TYPE;
    }

    @Override
    public String getVoidExcessType() {
        return "items_fluids";
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
    public boolean getVoidExcess() {
        return voidExcess;
    }

    @Override
    public void setVoidExcess(boolean voidExcess) {
        this.voidExcess = voidExcess;

        markDirty();
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
    public IItemHandler getFilterInventory() {
        return getType() == IType.ITEMS ? itemFilters : fluidFilters;
    }

    public ItemHandlerBase getItemFilters() {
        return itemFilters;
    }

    public ItemHandlerFluid getFluidFilters() {
        return fluidFilters;
    }

    @Override
    public IItemHandler getDrops() {
        return disks;
    }
}
