package com.refinedmods.refinedstorage.render.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PortableGridItemBakedModel implements IBakedModel {
    private final IBakedModel base;
    @Nullable
    private final IBakedModel disk;

    public PortableGridItemBakedModel(IBakedModel base, @Nullable IBakedModel disk) {
        this.base = base;
        this.disk = disk;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.EMPTY;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemCameraTransforms getTransforms() {
        return base.getTransforms();
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand) {
        List<BakedQuad> quads = new ArrayList<>(base.getQuads(state, side, rand));

        if (disk != null) {
            quads.addAll(disk.getQuads(state, side, rand));
        }

        return quads;
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
}
