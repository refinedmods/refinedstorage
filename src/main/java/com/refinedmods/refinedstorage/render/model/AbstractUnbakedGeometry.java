package com.refinedmods.refinedstorage.render.model;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

public abstract class AbstractUnbakedGeometry<T extends IUnbakedGeometry<T>> implements IUnbakedGeometry<T> {
    protected abstract Set<ResourceLocation> getModels();

    @Override
    public Collection<Material> getMaterials(final IGeometryBakingContext context,
                                             final Function<ResourceLocation, UnbakedModel> modelGetter,
                                             final Set<Pair<String, String>> missingTextureErrors) {
        return getModels()
            .stream()
            .map(modelGetter)
            .flatMap(unbakedModel -> unbakedModel.getMaterials(modelGetter, missingTextureErrors).stream())
            .toList();
    }
}
