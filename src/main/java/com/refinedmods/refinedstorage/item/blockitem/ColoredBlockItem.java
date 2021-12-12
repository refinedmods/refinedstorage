package com.refinedmods.refinedstorage.item.blockitem;

import com.refinedmods.refinedstorage.block.BaseBlock;
import com.refinedmods.refinedstorage.util.ColorMap;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import net.minecraft.item.Item.Properties;

public class ColoredBlockItem extends BaseBlockItem {
    private final ITextComponent displayName;

    public ColoredBlockItem(BaseBlock block, Properties builder, DyeColor color, ITextComponent displayName) {
        super(block, builder);

        if (color != ColorMap.DEFAULT_COLOR) {
            this.displayName = new TranslationTextComponent("color.minecraft." + color.getName())
                    .append(" ")
                    .append(displayName);
        } else {
            this.displayName = displayName;
        }

    }

    @Override
    public ITextComponent getName(ItemStack stack) {
        return displayName;
    }
}
