package com.raoulvdberge.refinedstorage.render.teisr;

import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;

public class TileEntityItemStackRendererPattern extends ItemStackTileEntityRenderer {
    @Override
    public void renderByItem(ItemStack stack) {
        /* TODO CraftingPattern pattern = ItemPattern.getPatternFromCache(null, stack);*/
        //ItemStack outputStack = pattern.getOutputs().get(0);

        //outputStack.getItem().getTileEntityItemStackRenderer().renderByItem(outputStack);
    }
}