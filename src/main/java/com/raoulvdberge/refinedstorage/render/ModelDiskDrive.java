package com.raoulvdberge.refinedstorage.render;

import com.google.common.base.Function;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.Collection;
import java.util.Collections;

public class ModelDiskDrive implements IModel {
    private static final ResourceLocation MODEL_BASE = new ResourceLocation("refinedstorage:block/disk_drive");
    private static final ResourceLocation MODEL_DISK = new ResourceLocation("refinedstorage:block/disk_drive_disk");
    private static final ResourceLocation MODEL_DISK_FULL = new ResourceLocation("refinedstorage:block/disk_drive_disk_full");
    private static final ResourceLocation MODEL_DISK_DISCONNECTED = new ResourceLocation("refinedstorage:block/disk_drive_disk_disconnected");

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return Collections.singletonList(MODEL_BASE);
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return Collections.emptyList();
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        IModel baseModel;
        IModel diskModel;
        IModel diskModelFull;
        IModel diskModelDisconnected;

        try {
            baseModel = ModelLoaderRegistry.getModel(MODEL_BASE);
            diskModel = ModelLoaderRegistry.getModel(MODEL_DISK);
            diskModelFull = ModelLoaderRegistry.getModel(MODEL_DISK_FULL);
            diskModelDisconnected = ModelLoaderRegistry.getModel(MODEL_DISK_DISCONNECTED);
        } catch (Exception e) {
            throw new Error("Unable to load disk drive models", e);
        }

        return new BakedModelDiskDrive(
            baseModel.bake(state, format, bakedTextureGetter),
            diskModel.bake(state, format, bakedTextureGetter),
            diskModelFull.bake(state, format, bakedTextureGetter),
            diskModelDisconnected.bake(state, format, bakedTextureGetter)
        );
    }

    @Override
    public IModelState getDefaultState() {
        return TRSRTransformation.identity();
    }
}
