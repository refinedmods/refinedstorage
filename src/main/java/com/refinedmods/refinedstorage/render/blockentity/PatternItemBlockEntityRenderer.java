package com.refinedmods.refinedstorage.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.item.PatternItem;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.RenderProperties;

public class PatternItemBlockEntityRenderer extends BlockEntityWithoutLevelRenderer {
    public PatternItemBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet entityModelSet) {
        super(dispatcher, entityModelSet);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrixStack, MultiBufferSource renderTypeBuffer, int combinedLight, int combinedOverlay) {
        ICraftingPattern pattern = PatternItem.fromCache(null, stack);

        ItemStack outputStack = pattern.getOutputs().get(0);

        RenderProperties.get(outputStack.getItem()).getItemStackRenderer().renderByItem(outputStack, transformType, matrixStack, renderTypeBuffer, combinedLight, combinedOverlay);
    }
}
