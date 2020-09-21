package com.refinedmods.refinedstorage.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ColoredNetworkBlock extends NetworkNodeBlock {

    public ColoredNetworkBlock(Properties props) {
        super(props);
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock().getClass().equals(newState.getBlock().getClass())) {
            return;
        }
        super.onReplaced(state, worldIn, pos, newState, isMoving);
    }
}

