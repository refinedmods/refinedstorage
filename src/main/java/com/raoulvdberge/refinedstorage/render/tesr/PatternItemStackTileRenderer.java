package com.raoulvdberge.refinedstorage.render.tesr;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.CraftingPattern;
import com.raoulvdberge.refinedstorage.item.PatternItem;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;

public class PatternItemStackTileRenderer extends ItemStackTileEntityRenderer {
    @Override
    public void render(ItemStack stack, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_228364_4_, int p_228364_5_) {
        CraftingPattern pattern = PatternItem.fromCache(null, stack);

        ItemStack outputStack = pattern.getOutputs().get(0);

        outputStack.getItem().getItemStackTileEntityRenderer().render(outputStack, matrixStack, renderTypeBuffer, p_228364_4_, p_228364_5_);
    }
}