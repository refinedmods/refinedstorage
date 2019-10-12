package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.RSTiles;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.diskmanipulator.NetworkNodeDiskManipulator;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.tile.config.IWhitelistBlacklist;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class TileDiskManipulator extends NetworkNodeTile<NetworkNodeDiskManipulator> {
    public static final TileDataParameter<Integer, TileDiskManipulator> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, TileDiskManipulator> WHITELIST_BLACKLIST = IWhitelistBlacklist.createParameter();
    public static final TileDataParameter<Integer, TileDiskManipulator> TYPE = IType.createParameter();
    public static final TileDataParameter<Integer, TileDiskManipulator> IO_MODE = new TileDataParameter<>(DataSerializers.VARINT, NetworkNodeDiskManipulator.IO_MODE_INSERT, t -> t.getNode().getIoMode(), (t, v) -> {
        t.getNode().setIoMode(v);
        t.getNode().markDirty();
    });

    private Integer[] diskState = new Integer[6];

    public TileDiskManipulator() {
        super(RSTiles.DISK_MANIPULATOR);

        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(WHITELIST_BLACKLIST);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(IO_MODE);

        // DiskDriveTile.initDiskState(diskState);
    }

    @Override
    public CompoundNBT writeUpdate(CompoundNBT tag) {
        super.writeUpdate(tag);

        // DiskDriveTile.writeDiskState(tag, 6, getNode().canUpdate(), getNode().getItemDisks(), getNode().getFluidDisks());

        return tag;
    }

    @Override
    public void readUpdate(CompoundNBT tag) {
        super.readUpdate(tag);

        // DiskDriveTile.readDiskState(tag, diskState);
    }

    public Integer[] getDiskState() {
        return diskState;
    }

   /* TODO  @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(getNode().getDisks());
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable Direction facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }*/

    @Override
    @Nonnull
    public NetworkNodeDiskManipulator createNode(World world, BlockPos pos) {
        return new NetworkNodeDiskManipulator(world, pos);
    }
}
