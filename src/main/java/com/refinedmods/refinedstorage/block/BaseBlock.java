package com.refinedmods.refinedstorage.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class BaseBlock extends Block {
    protected BaseBlock(Properties properties) {
        super(properties);
    }

    public BlockDirection getDirection() {
        return BlockDirection.NONE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot) {
        BlockDirection dir = getDirection();
        if (dir != BlockDirection.NONE) {
            Direction newDirection = dir.cycle(state.get(dir.getProperty()));

            return state.with(dir.getProperty(), newDirection);
        }

        return super.rotate(state, rot);
    }

    protected void onDirectionChanged(World world, BlockPos pos, Direction newDirection) {
        // NO OP
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onReplaced(state, world, pos, newState, isMoving);

        checkIfDirectionHasChanged(state, world, pos, newState);
    }

    protected void checkIfDirectionHasChanged(BlockState state, World world, BlockPos pos, BlockState newState) {
        if (getDirection() != BlockDirection.NONE &&
            state.getBlock() == newState.getBlock() &&
            state.get(getDirection().getProperty()) != newState.get(getDirection().getProperty())) {
            onDirectionChanged(world, pos, newState.get(getDirection().getProperty()));
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);

        BlockDirection dir = getDirection();
        if (dir != BlockDirection.NONE) {
            builder.add(dir.getProperty());
        }
    }
}
