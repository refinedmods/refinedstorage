package com.raoulvdberge.refinedstorage.render.tesr;

import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.CraftingPattern;
import com.raoulvdberge.refinedstorage.item.PatternItem;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;

public class PatternItemStackTileRenderer extends ItemStackTileEntityRenderer {
    @Override
    public void renderByItem(ItemStack stack) {
        CraftingPattern pattern = PatternItem.fromCache(null, stack);

        ItemStack outputStack = pattern.getOutputs().get(0);

        outputStack.getItem().getTileEntityItemStackRenderer().renderByItem(outputStack);
    }
}