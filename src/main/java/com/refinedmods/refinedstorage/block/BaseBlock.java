package com.refinedmods.refinedstorage.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraft.block.AbstractBlock.Properties;

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
            Direction newDirection = dir.cycle(state.getValue(dir.getProperty()));

            return state.setValue(dir.getProperty(), newDirection);
        }

        return super.rotate(state, rot);
    }

    protected void onDirectionChanged(World world, BlockPos pos, Direction newDirection) {
        // NO OP
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, world, pos, newState, isMoving);

        checkIfDirectionHasChanged(state, world, pos, newState);
    }

    protected void checkIfDirectionHasChanged(BlockState state, World world, BlockPos pos, BlockState newState) {
        if (getDirection() != BlockDirection.NONE &&
            state.getBlock() == newState.getBlock() &&
            state.getValue(getDirection().getProperty()) != newState.getValue(getDirection().getProperty())) {
            onDirectionChanged(world, pos, newState.getValue(getDirection().getProperty()));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);

        BlockDirection dir = getDirection();
        if (dir != BlockDirection.NONE) {
            builder.add(dir.getProperty());
        }
    }
}
