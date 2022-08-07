package com.refinedmods.refinedstorage.blockentity;

import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkReceiverNetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class NetworkReceiverBlockEntity extends NetworkNodeBlockEntity<NetworkReceiverNetworkNode> {
    public NetworkReceiverBlockEntity(BlockPos pos, BlockState state) {
        super(RSBlockEntities.NETWORK_RECEIVER.get(), pos, state);
    }

    @Override
    @Nonnull
    public NetworkReceiverNetworkNode createNode(Level level, BlockPos pos) {
        return new NetworkReceiverNetworkNode(level, pos);
    }
}
