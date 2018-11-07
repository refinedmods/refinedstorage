package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.diskmanipulator.NetworkNodeDiskManipulator;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileDiskManipulator extends TileNode<NetworkNodeDiskManipulator> {
    public static final TileDataParameter<Integer, TileDiskManipulator> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer, TileDiskManipulator> MODE = IFilterable.createParameter();
    public static final TileDataParameter<Integer, TileDiskManipulator> TYPE = IType.createParameter();
    public static final TileDataParameter<Integer, TileDiskManipulator> IO_MODE = new TileDataParameter<>(DataSerializers.VARINT, NetworkNodeDiskManipulator.IO_MODE_INSERT, t -> t.getNode().getIoMode(), (t, v) -> {
        t.getNode().setIoMode(v);
        t.getNode().markDirty();
    });

    private Integer[] diskState = new Integer[6];

    public TileDiskManipulator() {
        dataManager.addWatchedParameter(COMPARE);
        dataManager.addWatchedParameter(MODE);
        dataManager.addWatchedParameter(TYPE);
        dataManager.addWatchedParameter(IO_MODE);

        TileDiskDrive.initDiskState(diskState);
    }

    @Override
    public NBTTagCompound writeUpdate(NBTTagCompound tag) {
        super.writeUpdate(tag);

        TileDiskDrive.writeDiskState(tag, 6, getNode().canUpdate(), getNode().getItemDisks(), getNode().getFluidDisks());

        return tag;
    }

    @Override
    public void readUpdate(NBTTagCompound tag) {
        super.readUpdate(tag);

        TileDiskDrive.readDiskState(tag, diskState);
    }

    public Integer[] getDiskState() {
        return diskState;
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(getNode().getDisks());
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    @Nonnull
    public NetworkNodeDiskManipulator createNode(World world, BlockPos pos) {
        return new NetworkNodeDiskManipulator(world, pos);
    }

    @Override
    public String getNodeId() {
        return NetworkNodeDiskManipulator.ID;
    }
}
