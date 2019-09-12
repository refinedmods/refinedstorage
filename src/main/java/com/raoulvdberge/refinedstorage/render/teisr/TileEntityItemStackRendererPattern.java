package com.raoulvdberge.refinedstorage.render.teisr;

import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.CraftingPattern;
import com.raoulvdberge.refinedstorage.item.ItemPattern;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;

public class TileEntityItemStackRendererPattern extends ItemStackTileEntityRenderer {
    @Override
    public void renderByItem(ItemStack stack) {
        CraftingPattern pattern = ItemPattern.getPatternFromCache(null, stack);
        ItemStack outputStack = pattern.getOutputs().get(0);

        outputStack.getItem().getTileEntityItemStackRenderer().renderByItem(outputStack);
    }
}