package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeNetworkTransmitter;
import com.raoulvdberge.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileNetworkTransmitter extends TileNode<NetworkNodeNetworkTransmitter> {
    public static final TileDataParameter<Integer, TileNetworkTransmitter> DISTANCE = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> {
        NetworkNodeNetworkTransmitter transmitter = t.getNode();

        return (transmitter.getReceiver() != null && transmitter.isSameDimension()) ? transmitter.getDistance() : -1;
    });
    public static final TileDataParameter<Integer, TileNetworkTransmitter> RECEIVER_DIMENSION = new TileDataParameter<>(DataSerializers.VARINT, 0, t -> t.getNode().getReceiverDimension());

    public TileNetworkTransmitter() {
        dataManager.addWatchedParameter(DISTANCE);
        dataManager.addWatchedParameter(RECEIVER_DIMENSION);
    }

    @Override
    @Nonnull
    public NetworkNodeNetworkTransmitter createNode(World world, BlockPos pos) {
        return new NetworkNodeNetworkTransmitter(world, pos);
    }

    @Override
    public String getNodeId() {
        return NetworkNodeNetworkTransmitter.ID;
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
