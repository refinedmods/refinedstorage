package com.refinedmods.refinedstorage.blockentity;

import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.apiimpl.network.node.SecurityManagerNetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class SecurityManagerBlockEntity extends NetworkNodeBlockEntity<SecurityManagerNetworkNode> {
    public SecurityManagerBlockEntity(BlockPos pos, BlockState state) {
        super(RSBlockEntities.SECURITY_MANAGER, pos, state, SecurityManagerNetworkNode.class);
    }

    @Override
    @Nonnull
    public SecurityManagerNetworkNode createNode(Level level, BlockPos pos) {
        return new SecurityManagerNetworkNode(level, pos);
    }
}
