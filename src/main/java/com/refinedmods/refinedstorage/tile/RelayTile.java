package com.refinedmods.refinedstorage.tile;

import com.refinedmods.refinedstorage.RSTiles;
import com.refinedmods.refinedstorage.apiimpl.network.node.RelayNetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class RelayTile extends NetworkNodeTile<RelayNetworkNode> {
    public RelayTile(BlockPos pos, BlockState state) {
        super(RSTiles.RELAY, pos, state);
    }

    @Override
    @Nonnull
    public RelayNetworkNode createNode(Level world, BlockPos pos) {
        return new RelayNetworkNode(world, pos);
    }
}
