package com.refinedmods.refinedstorage.render.color;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.item.PatternItem;
import com.refinedmods.refinedstorage.render.model.PatternBakedModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;

public class PatternItemColor implements ItemColor {
    @Override
    public int getColor(ItemStack stack, int tintIndex) {
        ICraftingPattern pattern = PatternItem.fromCache(Minecraft.getInstance().level, stack);

        if (PatternBakedModel.canDisplayOutput(stack, pattern)) {
            int color = Minecraft.getInstance().getItemColors().getColor(pattern.getOutputs().get(0), tintIndex);

            if (color != -1) {
                return color;
            }
        }

        return 0xFFFFFF;
    }
}
