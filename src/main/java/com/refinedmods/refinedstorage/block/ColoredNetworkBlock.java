package com.refinedmods.refinedstorage.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraft.block.AbstractBlock.Properties;

public class ColoredNetworkBlock extends NetworkNodeBlock {
    public ColoredNetworkBlock(Properties props) {
        super(props);
    }

    // Don't do block drops if we change the color.
    @Override
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock().getClass().equals(newState.getBlock().getClass())) {
            checkIfDirectionHasChanged(state, world, pos, newState);
        } else {
            super.onRemove(state, world, pos, newState, isMoving);
        }
    }
}

