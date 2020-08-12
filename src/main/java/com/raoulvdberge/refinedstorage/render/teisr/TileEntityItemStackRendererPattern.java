package com.raoulvdberge.refinedstorage.render.teisr;

import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.CraftingPattern;
import com.raoulvdberge.refinedstorage.item.ItemPattern;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;

public class TileEntityItemStackRendererPattern extends TileEntityItemStackRenderer {
    @Override
    public void renderByItem(ItemStack stack) {
        CraftingPattern pattern = ItemPattern.getPatternFromCache(null, stack);
        ItemStack outputStack = pattern.getOutputs().get(0);

        outputStack.getItem().getTileEntityItemStackRenderer().renderByItem(outputStack);
    }

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        CraftingPattern pattern = ItemPattern.getPatternFromCache(null, stack);
        ItemStack outputStack = pattern.getOutputs().get(0);

        outputStack.getItem().getTileEntityItemStackRenderer().renderByItem(outputStack, partialTicks);
    }
}