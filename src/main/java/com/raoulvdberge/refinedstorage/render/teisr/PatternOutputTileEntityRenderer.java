package com.raoulvdberge.refinedstorage.render.teisr;

import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.CraftingPattern;
import com.raoulvdberge.refinedstorage.item.ItemPattern;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Used when BakedModelPattern requires a builtin renderer
 */
public class PatternOutputTileEntityRenderer extends TileEntityItemStackRenderer {
    public static TileEntityItemStackRenderer instance;

    @Override
    public void renderByItem(ItemStack stack) {
        CraftingPattern pattern = ItemPattern.getPatternFromCache((World)null, stack);
        ItemStack outputStack = pattern.getOutputs().get(0);
        outputStack.getItem().getTileEntityItemStackRenderer().renderByItem(outputStack);
    }

    @Override
    public void renderByItem(ItemStack stack, float partialTicks) {
        CraftingPattern pattern = ItemPattern.getPatternFromCache((World)null, stack);
        ItemStack outputStack = pattern.getOutputs().get(0);
        outputStack.getItem().getTileEntityItemStackRenderer().renderByItem(outputStack, partialTicks);
    }

    static
    {
        instance = new PatternOutputTileEntityRenderer();
    }
}
