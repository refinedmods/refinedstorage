package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDisk;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.diskdrive.NetworkNodeDiskDrive;
import com.raoulvdberge.refinedstorage.render.constants.ConstantsDisk;
import com.raoulvdberge.refinedstorage.tile.config.*;
import com.raoulvdberge.refinedstorage.tile.data.RSSerializers;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileDiskDrive extends TileNode<NetworkNodeDiskDrive> {
    public static final TileDataParameter<Integer, TileDiskDrive> PRIORITY = IPrioritizable.createParameter();
    public static final TileDataParameter<Integer, TileDiskDrive> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, TileDiskDrive> MODE = IFilterable.createParameter();
    public static final TileDataParameter<Integer, TileDiskDrive> TYPE = IType.createParameter();
    public static final TileDataParameter<AccessType, TileDiskDrive> ACCESS_TYPE = IAccessType.createParameter();
    public static final TileDataParameter<Long, TileDiskDrive> STORED = new TileDataParameter<>(RSSerializers.LONG_SERIALIZER, 0L, t -> {
        long stored = 0;

        for (IStorageDisk storage : t.getNode().getItemDisks()) {
            if (storage != null) {
                stored += storage.getStored();
            }
        }

        for (IStorageDisk storage : t.getNode().getFluidDisks()) {
            if (storage != null) {
                stored += storage.getStored();
            }
        }

        return stored;
    });
    public static final TileDataParameter<Long, TileDiskDrive> CAPACITY = new TileDataParameter<>(RSSerializers.LONG_SERIALIZER, 0L, t -> {
        long capacity = 0;

        for (IStorageDisk storage : t.getNode().getItemDisks()) {
            if (storage != null) {
                if (storage.getCapacity() == -1) {
                    return -1L;
                }

                capacity += storage.getCapacity();
            }
        }

        for (IStorageDisk storage : t.getNode().getFluidDisks()) {
            if (storage != null) {
                if (storage.getCapacity() == -1) {
                    return -1L;
                }

                capacity += storage.getCapacity();
            }
        }

        return capacity;
    });

    private static final String NBT_DISK_STATE = "DiskState_%d";

    private Integer[] diskState = new Integer[8];

    public TileDiskDrive() {
        dataManager.addWatchedParameter(PRIORITY);
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(MODE);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(ACCESS_TYPE);
        dataManager.addWatchedParameter(STORED);
        dataManager.addWatchedParameter(CAPACITY);

        initDiskState(diskState);
    }

    @Override
    public NBTTagCompound writeUpdate(NBTTagCompound tag) {
        super.writeUpdate(tag);

        writeDiskState(tag, 8, getNode().canUpdate(), getNode().getItemDisks(), getNode().getFluidDisks());

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

    public static void writeDiskState(NBTTagCompound tag, int disks, boolean connected, IStorageDisk[] itemStorages, IStorageDisk[] fluidStorages) {
        for (int i = 0; i < disks; ++i) {
            int state = ConstantsDisk.DISK_STATE_NONE;

            if (itemStorages[i] != null || fluidStorages[i] != null) {
                if (!connected) {
                    state = ConstantsDisk.DISK_STATE_DISCONNECTED;
                } else {
                    state = ConstantsDisk.getDiskState(
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
            diskState[i] = ConstantsDisk.DISK_STATE_NONE;
        }
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(getNode().getDisks());
        }

        return super.getCapability(capability, facing);
    }

    @Override
    @Nonnull
    public NetworkNodeDiskDrive createNode(World world, BlockPos pos) {
        return new NetworkNodeDiskDrive(world, pos);
    }

    @Override
    public String getNodeId() {
        return NetworkNodeDiskDrive.ID;
    }
}
