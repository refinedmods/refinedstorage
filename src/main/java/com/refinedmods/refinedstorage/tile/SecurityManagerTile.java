package com.refinedmods.refinedstorage.tile;

import com.refinedmods.refinedstorage.RSTiles;
import com.refinedmods.refinedstorage.apiimpl.network.node.SecurityManagerNetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class SecurityManagerTile extends NetworkNodeTile<SecurityManagerNetworkNode> {
    public SecurityManagerTile(BlockPos pos, BlockState state) {
        super(RSTiles.SECURITY_MANAGER, pos, state);
    }

    @Override
    @Nonnull
    public SecurityManagerNetworkNode createNode(Level level, BlockPos pos) {
        return new SecurityManagerNetworkNode(level, pos);
    }
}
