package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.api.network.INetworkNode;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeDiskManipulator;
import com.raoulvdberge.refinedstorage.tile.config.IComparable;
import com.raoulvdberge.refinedstorage.tile.config.IFilterable;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataConsumer;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataProducer;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

public class TileDiskManipulator extends TileNode {
    public static final TileDataParameter<Integer> COMPARE = IComparable.createParameter();
    public static final TileDataParameter<Integer> MODE = IFilterable.createParameter();
    public static final TileDataParameter<Integer> TYPE = IType.createParameter();

    public static final TileDataParameter<Integer> IO_MODE = new TileDataParameter<>(DataSerializers.VARINT, NetworkNodeDiskManipulator.IO_MODE_INSERT, new ITileDataProducer<Integer, TileDiskManipulator>() {
        @Override
        public Integer getValue(TileDiskManipulator tile) {
            return ((NetworkNodeDiskManipulator) tile.getNode()).getIoMode();
        }
    }, new ITileDataConsumer<Integer, TileDiskManipulator>() {
        @Override
        public void setValue(TileDiskManipulator tile, Integer value) {
            ((NetworkNodeDiskManipulator) tile.getNode()).setIoMode(value);
            tile.getNode().markDirty();
        }
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

        TileDiskDrive.writeDiskState(tag, 6, getNode().getNetwork() != null, ((NetworkNodeDiskManipulator) getNode()).getItemStorages(), ((NetworkNodeDiskManipulator) getNode()).getFluidStorages());

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
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(facing == EnumFacing.DOWN ? ((NetworkNodeDiskManipulator) getNode()).getOutputDisks() : ((NetworkNodeDiskManipulator) getNode()).getInputDisks());
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public INetworkNode createNode() {
        return new NetworkNodeDiskManipulator(this);
    }
}
