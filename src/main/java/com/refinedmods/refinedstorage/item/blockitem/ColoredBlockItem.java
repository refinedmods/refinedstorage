package com.refinedmods.refinedstorage.item.blockitem;

import com.refinedmods.refinedstorage.block.BaseBlock;
import com.refinedmods.refinedstorage.util.ColorMap;
import net.minecraft.network.chat.Component;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public class ColoredBlockItem extends BaseBlockItem {
    private final Component displayName;

    public ColoredBlockItem(BaseBlock block, Properties builder, DyeColor color, Component displayName) {
        super(block, builder);

        if (color != ColorMap.DEFAULT_COLOR) {
            this.displayName = Component.translatable("color.minecraft." + color.getName())
                .append(" ")
                .append(displayName);
        } else {
            this.displayName = displayName;
        }

    }

    @Override
    public Component getName(ItemStack stack) {
        return displayName;
    }
}
