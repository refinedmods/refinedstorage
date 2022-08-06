package com.refinedmods.refinedstorage.render.model;

import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.render.model.baked.DiskDriveBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;

import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DiskDriveUnbakedGeometry extends AbstractUnbakedGeometry<DiskDriveUnbakedGeometry> {
    private static final ResourceLocation BASE_MODEL = new ResourceLocation(RS.ID, "block/disk_drive_base");
    private static final ResourceLocation DISK_MODEL = new ResourceLocation(RS.ID, "block/disks/disk");
    private static final ResourceLocation DISK_DISCONNECTED_MODEL = new ResourceLocation(RS.ID, "block/disks/disk_disconnected");
    private static final ResourceLocation DISK_FULL_MODEL = new ResourceLocation(RS.ID, "block/disks/disk_full");
    private static final ResourceLocation DISK_NEAR_CAPACITY_MODEL = new ResourceLocation(RS.ID, "block/disks/disk_near_capacity");

    @Override
    protected Set<ResourceLocation> getModels() {
        return Set.of(
            BASE_MODEL,
            DISK_MODEL,
            DISK_DISCONNECTED_MODEL,
            DISK_FULL_MODEL,
            DISK_NEAR_CAPACITY_MODEL
        );
    }

    @Override
    public BakedModel bake(final IGeometryBakingContext context,
                           final ModelBakery bakery,
                           final Function<Material, TextureAtlasSprite> spriteGetter,
                           final ModelState modelState,
                           final ItemOverrides overrides,
                           final ResourceLocation modelLocation) {
        return new DiskDriveBakedModel(
            Objects.requireNonNull(bakery.bake(BASE_MODEL, modelState, spriteGetter)),
            getBaseModelBakery(modelState, bakery, spriteGetter),
            getDiskModelBakery(DISK_MODEL, modelState, bakery, spriteGetter),
            getDiskModelBakery(DISK_NEAR_CAPACITY_MODEL, modelState, bakery, spriteGetter),
            getDiskModelBakery(DISK_FULL_MODEL, modelState, bakery, spriteGetter),
            getDiskModelBakery(DISK_DISCONNECTED_MODEL, modelState, bakery, spriteGetter)
        );
    }

    private Function<Direction, BakedModel> getBaseModelBakery(final ModelState state,
                                                               final ModelBakery bakery,
                                                               final Function<Material, TextureAtlasSprite> sg) {
        return direction -> {
            final Transformation rotation = new Transformation(null, direction.getRotation(), null, null);
            final ModelState wrappedState = new SimpleModelState(rotation, state.isUvLocked());
            return bakery.bake(BASE_MODEL, wrappedState, sg);
        };
    }

    private BiFunction<Direction, Vector3f, BakedModel> getDiskModelBakery(final ResourceLocation id,
                                                                           final ModelState state,
                                                                           final ModelBakery bakery,
                                                                           final Function
                                                                               <Material, TextureAtlasSprite> sg) {
        return (direction, trans) -> {
            final Transformation translation = new Transformation(trans, null, null, null);
            final Transformation rotation = new Transformation(null, direction.getRotation(), null, null);
            final ModelState wrappedState = new SimpleModelState(rotation.compose(translation), state.isUvLocked());
            return bakery.bake(id, wrappedState, sg);
        };
    }
}
