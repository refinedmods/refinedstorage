package com.refinedmods.refinedstorage.render.tesr;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.refinedmods.refinedstorage.apiimpl.autocrafting.CraftingPattern;
import com.refinedmods.refinedstorage.item.PatternItem;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;

public class PatternItemStackTileRenderer extends ItemStackTileEntityRenderer {
    @Override
    public void func_239207_a_(ItemStack stack, ItemCameraTransforms.TransformType transformType, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, int p_239207_5_, int p_239207_6_) {
        CraftingPattern pattern = PatternItem.fromCache(null, stack);

        ItemStack outputStack = pattern.getOutputs().get(0);

        outputStack.getItem().getItemStackTileEntityRenderer().func_239207_a_(outputStack, transformType, matrixStack, renderTypeBuffer, p_239207_5_, p_239207_6_);
    }
}