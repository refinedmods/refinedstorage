package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeNetworkTransmitter;
import com.raoulvdberge.refinedstorage.tile.data.ITileDataProducer;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileNetworkTransmitter extends TileNode<NetworkNodeNetworkTransmitter> {
    public static final TileDataParameter<Integer> DISTANCE = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileNetworkTransmitter>() {
        @Override
        public Integer getValue(TileNetworkTransmitter tile) {
            NetworkNodeNetworkTransmitter transmitter = tile.getNode();

            return (transmitter.getReceiver() != null && transmitter.isSameDimension()) ? transmitter.getDistance() : -1;
        }
    });

    public static final TileDataParameter<Integer> RECEIVER_DIMENSION = new TileDataParameter<>(DataSerializers.VARINT, 0, new ITileDataProducer<Integer, TileNetworkTransmitter>() {
        @Override
        public Integer getValue(TileNetworkTransmitter tile) {
            return tile.getNode().getReceiverDimension();
        }
    });

    public static final TileDataParameter<Boolean> RECEIVER_DIMENSION_SUPPORTED = new TileDataParameter<>(DataSerializers.BOOLEAN, false, new ITileDataProducer<Boolean, TileNetworkTransmitter>() {
        @Override
        public Boolean getValue(TileNetworkTransmitter tile) {
            return tile.getNode().isDimensionSupported();
        }
    });

    public TileNetworkTransmitter() {
        dataManager.addWatchedParameter(DISTANCE);
        dataManager.addWatchedParameter(RECEIVER_DIMENSION);
        dataManager.addWatchedParameter(RECEIVER_DIMENSION_SUPPORTED);
    }

    @Override
    @Nonnull
    public NetworkNodeNetworkTransmitter createNode() {
        return new NetworkNodeNetworkTransmitter(this);
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(getNode().getNetworkCard());
        }

        return super.getCapability(capability, facing);
    }
}
