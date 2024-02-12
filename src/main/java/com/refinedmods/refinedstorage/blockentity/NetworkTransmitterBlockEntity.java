package com.refinedmods.refinedstorage.blockentity;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkTransmitterNetworkNode;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import com.refinedmods.refinedstorage.blockentity.data.RSSerializers;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nonnull;
import java.util.Optional;

public class NetworkTransmitterBlockEntity extends NetworkNodeBlockEntity<NetworkTransmitterNetworkNode> {
    public static final BlockEntitySynchronizationParameter<Integer, NetworkTransmitterBlockEntity> DISTANCE = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RS.ID, "network_transmitter_distance"), EntityDataSerializers.INT, 0, t -> t.getNode().getDistance());
    public static final BlockEntitySynchronizationParameter<Optional<ResourceLocation>, NetworkTransmitterBlockEntity> RECEIVER_DIMENSION = new BlockEntitySynchronizationParameter<>(new ResourceLocation(RS.ID, "network_transmitter_receiver_dimension"), RSSerializers.OPTIONAL_RESOURCE_LOCATION_SERIALIZER, Optional.empty(), t -> {
        if (t.getNode().getReceiverDimension() != null) {
            return Optional.of(t.getNode().getReceiverDimension().location());
        }

        return Optional.empty();
    });

    public static BlockEntitySynchronizationSpec SPEC = BlockEntitySynchronizationSpec.builder()
        .addWatchedParameter(REDSTONE_MODE)
        .addWatchedParameter(DISTANCE)
        .addWatchedParameter(RECEIVER_DIMENSION)
        .build();

    public NetworkTransmitterBlockEntity(BlockPos pos, BlockState state) {
        super(RSBlockEntities.NETWORK_TRANSMITTER.get(), pos, state, SPEC, NetworkTransmitterNetworkNode.class);
    }

    @Override
    @Nonnull
    public NetworkTransmitterNetworkNode createNode(Level level, BlockPos pos) {
        return new NetworkTransmitterNetworkNode(level, pos);
    }
}
