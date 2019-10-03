package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.block.info.BlockDirection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Rotation;

public abstract class BaseBlock extends Block {
    public BaseBlock(Properties properties) {
        super(properties);
    }

    public BlockDirection getDirection() {
        return BlockDirection.NONE;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        BlockDirection dir = getDirection();
        if (dir != BlockDirection.NONE) {
            return state.with(dir.getProperty(), dir.cycle(state.get(dir.getProperty())));
        }

        return super.rotate(state, rot);
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
