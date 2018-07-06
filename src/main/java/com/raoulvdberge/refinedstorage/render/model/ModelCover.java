package com.raoulvdberge.refinedstorage.render.model;

import com.raoulvdberge.refinedstorage.render.model.baked.BakedModelCover;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.common.model.IModelState;

import java.util.function.Function;

public class ModelCover implements IModel {
    private boolean hollow;

    public ModelCover(boolean hollow) {
        this.hollow = hollow;
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        return new BakedModelCover(null, hollow);
    }
}
