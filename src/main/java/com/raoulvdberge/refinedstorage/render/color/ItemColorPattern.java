package com.raoulvdberge.refinedstorage.render.color;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;

public class ItemColorPattern implements IItemColor {
    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        // TODO CraftingPattern pattern = ItemPattern.getPatternFromCache(Minecraft.getInstance().world, stack);

        /*
        if (BakedModelPattern.canDisplayOutput(stack, pattern)) {
            int color = Minecraft.getInstance().getItemColors().getColor(pattern.getOutputs().get(0), tintIndex);

            if (color != -1) {
                return color;
            }
        }*/

        return 0xFFFFFF;
    }
}
