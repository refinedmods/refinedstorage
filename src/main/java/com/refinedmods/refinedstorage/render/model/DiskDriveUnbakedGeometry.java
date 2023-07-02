package com.refinedmods.refinedstorage.render.model;

import com.mojang.math.Transformation;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.render.model.baked.DiskDriveBakedModel;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import org.joml.Vector3f;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DiskDriveUnbakedGeometry implements IUnbakedGeometry<DiskDriveUnbakedGeometry> {
    private static final ResourceLocation BASE_MODEL = new ResourceLocation(RS.ID, "block/disk_drive_base");
    private static final ResourceLocation DISK_MODEL = new ResourceLocation(RS.ID, "block/disks/disk");
    private static final ResourceLocation DISK_DISCONNECTED_MODEL = new ResourceLocation(RS.ID, "block/disks/disk_disconnected");
    private static final ResourceLocation DISK_FULL_MODEL = new ResourceLocation(RS.ID, "block/disks/disk_full");
    private static final ResourceLocation DISK_NEAR_CAPACITY_MODEL = new ResourceLocation(RS.ID, "block/disks/disk_near_capacity");

    @Override
    public void resolveParents(final Function<ResourceLocation, UnbakedModel> modelGetter,
                               final IGeometryBakingContext context) {
        modelGetter.apply(BASE_MODEL).resolveParents(modelGetter);
        modelGetter.apply(DISK_MODEL).resolveParents(modelGetter);
        modelGetter.apply(DISK_DISCONNECTED_MODEL).resolveParents(modelGetter);
        modelGetter.apply(DISK_FULL_MODEL).resolveParents(modelGetter);
        modelGetter.apply(DISK_NEAR_CAPACITY_MODEL).resolveParents(modelGetter);
    }

    @Override
    public BakedModel bake(final IGeometryBakingContext context,
                           final ModelBaker baker,
                           final Function<Material, TextureAtlasSprite> spriteGetter,
                           final ModelState modelState,
                           final ItemOverrides overrides,
                           final ResourceLocation modelLocation) {
        return new DiskDriveBakedModel(
            Objects.requireNonNull(baker.bake(BASE_MODEL, modelState, spriteGetter)),
            getBaseModelBaker(modelState, baker, spriteGetter),
            getDiskModelBaker(DISK_MODEL, modelState, baker, spriteGetter),
            getDiskModelBaker(DISK_NEAR_CAPACITY_MODEL, modelState, baker, spriteGetter),
            getDiskModelBaker(DISK_FULL_MODEL, modelState, baker, spriteGetter),
            getDiskModelBaker(DISK_DISCONNECTED_MODEL, modelState, baker, spriteGetter)
        );
    }

    private Function<Direction, BakedModel> getBaseModelBaker(final ModelState state,
                                                              final ModelBaker baker,
                                                              final Function<Material, TextureAtlasSprite> sg) {
        return direction -> {
            final Transformation rotation = new Transformation(null, RenderUtils.getQuaternion(direction), null, null);
            final ModelState wrappedState = new SimpleModelState(rotation, state.isUvLocked());
            return baker.bake(BASE_MODEL, wrappedState, sg);
        };
    }

    private BiFunction<Direction, Vector3f, BakedModel> getDiskModelBaker(final ResourceLocation id,
                                                                          final ModelState state,
                                                                          final ModelBaker baker,
                                                                          final Function
                                                                              <Material, TextureAtlasSprite> sg) {
        return (direction, trans) -> {
            final Transformation translation = new Transformation(trans, null, null, null);
            final Transformation rotation = new Transformation(null, RenderUtils.getQuaternion(direction), null, null);
            final ModelState wrappedState = new SimpleModelState(rotation.compose(translation), state.isUvLocked());
            return baker.bake(id, wrappedState, sg);
        };
    }
}
