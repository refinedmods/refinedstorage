package com.refinedmods.refinedstorage.blockentity;

import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.apiimpl.network.node.SecurityManagerNetworkNode;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class SecurityManagerBlockEntity extends NetworkNodeBlockEntity<SecurityManagerNetworkNode> {
    public static BlockEntitySynchronizationSpec SPEC = BlockEntitySynchronizationSpec.builder()
        .addWatchedParameter(REDSTONE_MODE)
        .build();

    public SecurityManagerBlockEntity(BlockPos pos, BlockState state) {
        super(RSBlockEntities.SECURITY_MANAGER.get(), pos, state, SPEC);
    }

    @Override
    @Nonnull
    public SecurityManagerNetworkNode createNode(Level level, BlockPos pos) {
        return new SecurityManagerNetworkNode(level, pos);
    }
}
