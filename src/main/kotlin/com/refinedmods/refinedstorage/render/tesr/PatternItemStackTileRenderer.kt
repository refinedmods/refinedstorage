package com.refinedmods.refinedstorage.render.tesr

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.item.PatternItem.Companion.fromCache
import net.minecraft.client.renderer.IRenderTypeBuffer
import net.minecraft.client.renderer.model.ItemCameraTransforms
import net.minecraft.client.renderer.tileentity.ItemStackBlockEntityRenderer
import net.minecraft.item.ItemStack

class PatternItemStackTileRenderer : ItemStackBlockEntityRenderer() {
    fun func_239207_a_(stack: ItemStack?, transformType: ItemCameraTransforms.TransformType?, matrixStack: MatrixStack?, renderTypeBuffer: IRenderTypeBuffer?, p_239207_5_: Int, p_239207_6_: Int) {
        val pattern = fromCache(null, stack!!)
        val outputStack: ItemStack = pattern!!.getOutputs().get(0)
        outputStack.item.getItemStackBlockEntityRenderer().func_239207_a_(outputStack, transformType, matrixStack, renderTypeBuffer, p_239207_5_, p_239207_6_)
    }
}