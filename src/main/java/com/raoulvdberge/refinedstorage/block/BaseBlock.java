package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.block.info.BlockDirection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;

public abstract class BaseBlock extends Block {
    public BaseBlock(Properties properties) {
        super(properties);
    }

    public BlockDirection getDirection() {
        return BlockDirection.NONE;
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
