package com.refinedmods.refinedstorage.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.item.PatternItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class PatternItemBlockEntityRenderer extends BlockEntityWithoutLevelRenderer {
    private static PatternItemBlockEntityRenderer instance;

    public PatternItemBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet entityModelSet) {
        super(dispatcher, entityModelSet);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource renderTypeBuffer, int combinedLight, int combinedOverlay) {
        ICraftingPattern pattern = PatternItem.fromCache(Minecraft.getInstance().level, stack);

        ItemStack outputStack = pattern.getOutputs().get(0);

        IClientItemExtensions.of(outputStack.getItem()).getCustomRenderer()
            .renderByItem(outputStack, context, poseStack, renderTypeBuffer, combinedLight, combinedOverlay);
    }

    public static PatternItemBlockEntityRenderer getInstance() {
        if (instance == null) {
            instance = new PatternItemBlockEntityRenderer(
                Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels()
            );
        }
        return instance;
    }
}
