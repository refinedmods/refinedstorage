package com.raoulvdberge.refinedstorage.render.color;

import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.CraftingPattern;
import com.raoulvdberge.refinedstorage.item.ItemPattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;

public class ItemColorPattern implements IItemColor {
    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        CraftingPattern pattern = ItemPattern.getPatternFromCache(Minecraft.getInstance().world, stack);

        if (/*TODO BakedModelPattern.canDisplayOutput(stack, pattern)*/false) {
            int color = Minecraft.getInstance().getItemColors().getColor(pattern.getOutputs().get(0), tintIndex);

            if (color != -1) {
                return color;
            }
        }

        return 0xFFFFFF;
    }
}
