package com.raoulvdberge.refinedstorage.render.model;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.CoverType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

public class ModelCover implements IUnbakedModel {
    private CoverType coverType;

    public ModelCover(CoverType coverType) {
        this.coverType = coverType;
    }

    @Nullable
    @Override
    public IBakedModel bake(ModelBakery bakery, Function spriteGetter, ISprite sprite, VertexFormat format) {
        return null;
        //return new BakedModelCover(null, coverType);
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return new ArrayList<>();
    }

    @Override
    public Collection<ResourceLocation> getTextures(Function<ResourceLocation, IUnbakedModel> modelGetter, Set<String> missingTextureErrors) {
        return new ArrayList<>();
    }
}
