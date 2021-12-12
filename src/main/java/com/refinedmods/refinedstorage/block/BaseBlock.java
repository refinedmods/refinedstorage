package com.refinedmods.refinedstorage.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

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

    protected void onDirectionChanged(Level world, BlockPos pos, Direction newDirection) {
        // NO OP
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, world, pos, newState, isMoving);

        checkIfDirectionHasChanged(state, world, pos, newState);
    }

    protected void checkIfDirectionHasChanged(BlockState state, Level world, BlockPos pos, BlockState newState) {
        if (getDirection() != BlockDirection.NONE &&
            state.getBlock() == newState.getBlock() &&
            state.getValue(getDirection().getProperty()) != newState.getValue(getDirection().getProperty())) {
            onDirectionChanged(world, pos, newState.getValue(getDirection().getProperty()));
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);

        BlockDirection dir = getDirection();
        if (dir != BlockDirection.NONE) {
            builder.add(dir.getProperty());
        }
    }
}
