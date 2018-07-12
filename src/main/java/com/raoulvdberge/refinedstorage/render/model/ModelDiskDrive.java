package com.raoulvdberge.refinedstorage.render.model;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.render.model.baked.BakedModelDiskDrive;
import com.raoulvdberge.refinedstorage.render.model.baked.BakedModelFullbright;
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
    private static final ResourceLocation MODEL_BASE = new ResourceLocation(RS.ID + ":block/disk_drive");

    private static final ResourceLocation MODEL_DISK = new ResourceLocation(RS.ID + ":block/disks/disk");
    private static final ResourceLocation MODEL_DISK_NEAR_CAPACITY = new ResourceLocation(RS.ID + ":block/disks/disk_near_capacity");
    private static final ResourceLocation MODEL_DISK_FULL = new ResourceLocation(RS.ID + ":block/disks/disk_full");
    private static final ResourceLocation MODEL_DISK_DISCONNECTED = new ResourceLocation(RS.ID + ":block/disks/disk_disconnected");

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
            new BakedModelFullbright(diskModel.bake(state, format, bakedTextureGetter), RS.ID + ":blocks/disks/leds").setCacheDisabled(),
            new BakedModelFullbright(diskModelNearCapacity.bake(state, format, bakedTextureGetter), RS.ID + ":blocks/disks/leds").setCacheDisabled(),
            new BakedModelFullbright(diskModelFull.bake(state, format, bakedTextureGetter), RS.ID + ":blocks/disks/leds").setCacheDisabled(),
            diskModelDisconnected.bake(state, format, bakedTextureGetter)
        );
    }
}
