package com.refinedmods.refinedstorage.render.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class DelegateBakedModel implements IBakedModel {
    protected final IBakedModel base;

    public DelegateBakedModel(IBakedModel base) {
        this.base = base;
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        return base.getQuads(state, side, rand);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return base.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return base.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return base.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return base.isCustomRenderer();
    }

    @Override
    @SuppressWarnings("deprecation")
    public TextureAtlasSprite getParticleIcon() {
        return base.getParticleIcon();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return base.getOverrides();
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemCameraTransforms getTransforms() {
        return base.getTransforms();
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBakedModel handlePerspective(ItemCameraTransforms.TransformType cameraTransformType, MatrixStack matrixStack) {
        return base.handlePerspective(cameraTransformType, matrixStack);
    }
}
