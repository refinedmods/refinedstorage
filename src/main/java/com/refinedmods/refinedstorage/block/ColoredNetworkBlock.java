package com.refinedmods.refinedstorage.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ColoredNetworkBlock extends NetworkNodeBlock {
    public ColoredNetworkBlock(Properties props) {
        super(props);
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock().getClass().equals(newState.getBlock().getClass())) {
            //From BaseBlock#onReplaced as this gets skipped otherwise
            if (getDirection() != BlockDirection.NONE &&
                state.getBlock() == newState.getBlock() &&
                state.get(getDirection().getProperty()) != newState.get(getDirection().getProperty())) {
                onDirectionChanged(world, pos, newState.get(getDirection().getProperty()));
            }
            return;
        }

        super.onReplaced(state, world, pos, newState, isMoving);
    }
}

