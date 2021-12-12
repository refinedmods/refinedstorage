package com.refinedmods.refinedstorage.tile;

import com.refinedmods.refinedstorage.RSTiles;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkTransmitterNetworkNode;
import com.refinedmods.refinedstorage.tile.data.RSSerializers;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class NetworkTransmitterTile extends NetworkNodeTile<NetworkTransmitterNetworkNode> {
    public static final TileDataParameter<Integer, NetworkTransmitterTile> DISTANCE = new TileDataParameter<>(EntityDataSerializers.INT, 0, t -> t.getNode().getDistance());
    public static final TileDataParameter<Optional<ResourceLocation>, NetworkTransmitterTile> RECEIVER_DIMENSION = new TileDataParameter<>(RSSerializers.OPTIONAL_RESOURCE_LOCATION_SERIALIZER, Optional.empty(), t -> {
        if (t.getNode().getReceiverDimension() != null) {
            return Optional.of(t.getNode().getReceiverDimension().location());
        }

        return Optional.empty();
    });

    private final LazyOptional<IItemHandler> networkCardCapability = LazyOptional.of(() -> getNode().getNetworkCard());

    public NetworkTransmitterTile(BlockPos pos, BlockState state) {
        super(RSTiles.NETWORK_TRANSMITTER, pos, state);

        dataManager.addWatchedParameter(DISTANCE);
        dataManager.addWatchedParameter(RECEIVER_DIMENSION);
    }

    @Override
    @Nonnull
    public NetworkTransmitterNetworkNode createNode(Level world, BlockPos pos) {
        return new NetworkTransmitterNetworkNode(world, pos);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction direction) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return networkCardCapability.cast();
        }

        return super.getCapability(cap, direction);
    }
}
