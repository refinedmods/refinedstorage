package com.refinedmods.refinedstorage.item.blockitem;

import com.refinedmods.refinedstorage.block.BaseBlock;
import com.refinedmods.refinedstorage.block.BlockDirection;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;

import net.minecraft.item.Item.Properties;

public class BaseBlockItem extends BlockItem {
    private final BaseBlock block;

    public BaseBlockItem(BaseBlock block, Properties builder) {
        super(block, builder);

        this.block = block;
    }

    @Override
    protected boolean placeBlock(BlockItemUseContext context, BlockState state) {
        boolean result = super.placeBlock(context, state);

        if (result && block.getDirection() != BlockDirection.NONE) {
            context.getLevel().setBlockAndUpdate(context.getClickedPos(), state.setValue(block.getDirection().getProperty(), block.getDirection().getFrom(
                context.getClickedFace(),
                context.getClickedPos(),
                context.getPlayer()
            )));
        }

        return result;
    }
}
