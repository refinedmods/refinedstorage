package com.raoulvdberge.refinedstorage.render.model;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.render.model.baked.BakedModelDiskManipulator;
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
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class ModelDiskManipulator implements IModel {
    private static final ResourceLocation MODEL_BASE_CUTOUT = new ResourceLocation(RS.ID + ":block/cube_north_cutout");
    private static final ResourceLocation MODEL_BASE_CONNECTED = new ResourceLocation(RS.ID + ":block/disk_manipulator_connected");
    private static final ResourceLocation MODEL_BASE_DISCONNECTED = new ResourceLocation(RS.ID + ":block/disk_manipulator_disconnected");

    private static final ResourceLocation MODEL_DISK = new ResourceLocation(RS.ID + ":block/disks/disk");
    private static final ResourceLocation MODEL_DISK_NEAR_CAPACITY = new ResourceLocation(RS.ID + ":block/disks/disk_near_capacity");
    private static final ResourceLocation MODEL_DISK_FULL = new ResourceLocation(RS.ID + ":block/disks/disk_full");
    private static final ResourceLocation MODEL_DISK_DISCONNECTED = new ResourceLocation(RS.ID + ":block/disks/disk_disconnected");

    @Override
    public Collection<ResourceLocation> getDependencies() {
        List<ResourceLocation> dependencies = new ArrayList<>();

        dependencies.add(MODEL_BASE_CUTOUT);
        dependencies.add(MODEL_BASE_CONNECTED);
        dependencies.add(MODEL_BASE_DISCONNECTED);
        dependencies.add(MODEL_DISK);
        dependencies.add(MODEL_DISK_NEAR_CAPACITY);
        dependencies.add(MODEL_DISK_FULL);
        dependencies.add(MODEL_DISK_DISCONNECTED);

        return dependencies;
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        return Collections.emptyList();
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        IModel baseModelConnected, baseModelDisconnected;
        IModel diskModel;
        IModel diskModelNearCapacity;
        IModel diskModelFull;
        IModel diskModelDisconnected;

        try {
            baseModelConnected = ModelLoaderRegistry.getModel(MODEL_BASE_CONNECTED);
            baseModelDisconnected = ModelLoaderRegistry.getModel(MODEL_BASE_DISCONNECTED);
            diskModel = ModelLoaderRegistry.getModel(MODEL_DISK);
            diskModelNearCapacity = ModelLoaderRegistry.getModel(MODEL_DISK_NEAR_CAPACITY);
            diskModelFull = ModelLoaderRegistry.getModel(MODEL_DISK_FULL);
            diskModelDisconnected = ModelLoaderRegistry.getModel(MODEL_DISK_DISCONNECTED);
        } catch (Exception e) {
            throw new Error("Unable to load disk manipulator models", e);
        }

        return new BakedModelDiskManipulator(
            new BakedModelFullbright(baseModelConnected.bake(state, format, bakedTextureGetter), RS.ID + ":blocks/disk_manipulator/cutouts/connected"),
            baseModelDisconnected.bake(state, format, bakedTextureGetter),
            new BakedModelFullbright(diskModel.bake(state, format, bakedTextureGetter), RS.ID + ":blocks/disks/leds").setCacheDisabled(),
            new BakedModelFullbright(diskModelNearCapacity.bake(state, format, bakedTextureGetter), RS.ID + ":blocks/disks/leds").setCacheDisabled(),
            new BakedModelFullbright(diskModelFull.bake(state, format, bakedTextureGetter), RS.ID + ":blocks/disks/leds").setCacheDisabled(),
            diskModelDisconnected.bake(state, format, bakedTextureGetter)
        );
    }
}
