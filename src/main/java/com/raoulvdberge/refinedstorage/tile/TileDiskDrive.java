package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeDiskDrive;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageFluidNBT;
import com.raoulvdberge.refinedstorage.apiimpl.storage.StorageItemNBT;
import com.raoulvdberge.refinedstorage.tile.config.*;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileDiskDrive extends TileNode<NetworkNodeDiskDrive> {
    public static final TileDataParameter<Integer> PRIORITY = IPrioritizable.createParameter();
    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer> MODE = IFilterable.createParameter();
    public static final TileDataParameter<Integer> TYPE = IType.createParameter();
    public static final TileDataParameter<Boolean> VOID_EXCESS = IExcessVoidable.createParameter();
    public static final TileDataParameter<AccessType> ACCESS_TYPE = IAccessType.createParameter();

    private static final String NBT_DISK_STATE = "DiskState_%d";

    public static final int DISK_STATE_NORMAL = 0;
    public static final int DISK_STATE_NEAR_CAPACITY = 1;
    public static final int DISK_STATE_FULL = 2;
    public static final int DISK_STATE_DISCONNECTED = 3;
    public static final int DISK_STATE_NONE = 4;

    private Integer[] diskState = new Integer[8];

    public TileDiskDrive() {
        dataManager.addWatchedParameter(PRIORITY);
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(MODE);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(VOID_EXCESS);
        dataManager.addWatchedParameter(ACCESS_TYPE);

        initDiskState(diskState);
    }

    @Override
    public NBTTagCompound writeUpdate(NBTTagCompound tag) {
        super.writeUpdate(tag);

        writeDiskState(tag, 8, getNode().getNetwork() != null, getNode().getItemStorages(), getNode().getFluidStorages());

        return tag;
    }

    @Override
    public void readUpdate(NBTTagCompound tag) {
        super.readUpdate(tag);

        readDiskState(tag, diskState);
    }

    public Integer[] getDiskState() {
        return diskState;
    }

    public static void writeDiskState(NBTTagCompound tag, int disks, boolean connected, StorageItemNBT[] itemStorages, StorageFluidNBT[] fluidStorages) {
        for (int i = 0; i < disks; ++i) {
            int state = DISK_STATE_NONE;

            if (itemStorages[i] != null || fluidStorages[i] != null) {
                if (!connected) {
                    state = DISK_STATE_DISCONNECTED;
                } else {
                    state = getDiskState(
                            itemStorages[i] != null ? itemStorages[i].getStored() : fluidStorages[i].getStored(),
                            itemStorages[i] != null ? itemStorages[i].getCapacity() : fluidStorages[i].getCapacity()
                    );
                }
            }

            tag.setInteger(String.format(NBT_DISK_STATE, i), state);
        }
    }

    public static void readDiskState(NBTTagCompound tag, Integer[] diskState) {
        for (int i = 0; i < diskState.length; ++i) {
            diskState[i] = tag.getInteger(String.format(NBT_DISK_STATE, i));
        }
    }

    public static void initDiskState(Integer[] diskState) {
        for (int i = 0; i < diskState.length; ++i) {
            diskState[i] = DISK_STATE_NONE;
        }
    }

    public static int getDiskState(int stored, int capacity) {
        if (stored == capacity) {
            return DISK_STATE_FULL;
        } else if ((int) ((float) stored / (float) capacity * 100F) >= 85) {
            return DISK_STATE_NEAR_CAPACITY;
        } else {
            return DISK_STATE_NORMAL;
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(getNode().getDisks());
        }

        return super.getCapability(capability, facing);
    }

    @Override
    @Nonnull
    public NetworkNodeDiskDrive createNode() {
        return new NetworkNodeDiskDrive(this);
    }
}
