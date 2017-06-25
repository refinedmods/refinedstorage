package com.raoulvdberge.refinedstorage.render;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class ModelDiskDrive implements IModel {
    private static final ResourceLocation MODEL_BASE = new ResourceLocation("refinedstorage:block/disk_drive");
    private static final ResourceLocation MODEL_DISK = new ResourceLocation("refinedstorage:block/disk");
    private static final ResourceLocation MODEL_DISK_NEAR_CAPACITY = new ResourceLocation("refinedstorage:block/disk_near_capacity");
    private static final ResourceLocation MODEL_DISK_FULL = new ResourceLocation("refinedstorage:block/disk_full");
    private static final ResourceLocation MODEL_DISK_DISCONNECTED = new ResourceLocation("refinedstorage:block/disk_disconnected");

    @Override
    public Collection<ResourceLocation> getDependencies() {
        List<ResourceLocation> dependencies = new ArrayList<>();

        dependencies.add(MODEL_BASE);
        dependencies.add(MODEL_DISK);
        dependencies.add(MODEL_DISK_NEAR_CAPACITY);
        dependencies.add(MODEL_DISK_FULL);
        dependencies.add(MODEL_DISK_DISCONNECTED);

        return dependencies;
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        IModel baseModel;
        IModel diskModel;
        IModel diskModelNearCapacity;
        IModel diskModelFull;
        IModel diskModelDisconnected;

        try {
            baseModel = ModelLoaderRegistry.getModel(MODEL_BASE);
            diskModel = ModelLoaderRegistry.getModel(MODEL_DISK);
            diskModelNearCapacity = ModelLoaderRegistry.getModel(MODEL_DISK_NEAR_CAPACITY);
            diskModelFull = ModelLoaderRegistry.getModel(MODEL_DISK_FULL);
            diskModelDisconnected = ModelLoaderRegistry.getModel(MODEL_DISK_DISCONNECTED);
        } catch (Exception e) {
            throw new Error("Unable to load disk drive models", e);
        }

        return new BakedModelDiskDrive(
            baseModel.bake(state, format, bakedTextureGetter),
            diskModel.bake(state, format, bakedTextureGetter),
            diskModelNearCapacity.bake(state, format, bakedTextureGetter),
            diskModelFull.bake(state, format, bakedTextureGetter),
            diskModelDisconnected.bake(state, format, bakedTextureGetter)
        );
    }
}
