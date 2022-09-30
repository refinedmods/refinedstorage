package com.refinedmods.refinedstorage.blockentity;

import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkReceiverNetworkNode;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class NetworkReceiverBlockEntity extends NetworkNodeBlockEntity<NetworkReceiverNetworkNode> {
    public static BlockEntitySynchronizationSpec SPEC = BlockEntitySynchronizationSpec.builder()
        .addWatchedParameter(REDSTONE_MODE)
        .build();

    public NetworkReceiverBlockEntity(BlockPos pos, BlockState state) {
        super(RSBlockEntities.NETWORK_RECEIVER.get(), pos, state, SPEC);
    }

    @Override
    @Nonnull
    public NetworkReceiverNetworkNode createNode(Level level, BlockPos pos) {
        return new NetworkReceiverNetworkNode(level, pos);
    }
}
