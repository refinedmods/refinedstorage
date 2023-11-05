package com.refinedmods.refinedstorage.blockentity;

import com.refinedmods.refinedstorage.RSBlockEntities;
import com.refinedmods.refinedstorage.apiimpl.network.node.RelayNetworkNode;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class RelayBlockEntity extends NetworkNodeBlockEntity<RelayNetworkNode> {
    public static BlockEntitySynchronizationSpec SPEC = BlockEntitySynchronizationSpec.builder()
        .addWatchedParameter(REDSTONE_MODE)
        .build();

    public RelayBlockEntity(BlockPos pos, BlockState state) {
        super(RSBlockEntities.RELAY.get(), pos, state, SPEC, RelayNetworkNode.class);
    }

    @Override
    @Nonnull
    public RelayNetworkNode createNode(Level level, BlockPos pos) {
        return new RelayNetworkNode(level, pos);
    }
}
