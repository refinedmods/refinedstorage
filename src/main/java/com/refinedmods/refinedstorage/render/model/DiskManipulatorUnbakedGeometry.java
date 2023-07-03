package com.refinedmods.refinedstorage.render.model;

import com.mojang.math.Transformation;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.render.model.baked.DiskManipulatorBakedModel;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DiskManipulatorUnbakedGeometry implements IUnbakedGeometry<DiskManipulatorUnbakedGeometry> {
    private static final ResourceLocation BASE_MODEL_DISCONNECTED = new ResourceLocation(RS.ID, "block/disk_manipulator/disconnected");

    private final Map<DyeColor, ResourceLocation> BASE_MODEL_CONNECTED = new HashMap<>();
    private static final ResourceLocation DISK_MODEL = new ResourceLocation(RS.ID, "block/disks/disk");
    private static final ResourceLocation DISK_DISCONNECTED_MODEL = new ResourceLocation(RS.ID, "block/disks/disk_disconnected");
    private static final ResourceLocation DISK_FULL_MODEL = new ResourceLocation(RS.ID, "block/disks/disk_full");
    private static final ResourceLocation DISK_NEAR_CAPACITY_MODEL = new ResourceLocation(RS.ID, "block/disks/disk_near_capacity");

    public DiskManipulatorUnbakedGeometry() {
        for (DyeColor value : DyeColor.values()) {
            BASE_MODEL_CONNECTED.put(value, new ResourceLocation(RS.ID, "block/disk_manipulator/" + value.getName()));
        }
    }

    @Override
    public void resolveParents(final Function<ResourceLocation, UnbakedModel> modelGetter,
                               final IGeometryBakingContext context) {
        modelGetter.apply(BASE_MODEL_DISCONNECTED).resolveParents(modelGetter);
        modelGetter.apply(DISK_MODEL).resolveParents(modelGetter);
        modelGetter.apply(DISK_DISCONNECTED_MODEL).resolveParents(modelGetter);
        modelGetter.apply(DISK_FULL_MODEL).resolveParents(modelGetter);
        modelGetter.apply(DISK_NEAR_CAPACITY_MODEL).resolveParents(modelGetter);
        BASE_MODEL_CONNECTED.values().forEach(m -> modelGetter.apply(m).resolveParents(modelGetter));
    }

    @Override
    public BakedModel bake(final IGeometryBakingContext context,
                           final ModelBaker baker,
                           final Function<Material, TextureAtlasSprite> spriteGetter,
                           final ModelState modelState,
                           final ItemOverrides overrides,
                           final ResourceLocation modelLocation) {
        var renderTypeHint = context.getRenderTypeHint();
        var renderTypes = renderTypeHint != null ? context.getRenderType(renderTypeHint) : RenderTypeGroup.EMPTY;

        return new DiskManipulatorBakedModel(
            Objects.requireNonNull(baker.bake(BASE_MODEL_DISCONNECTED, modelState, spriteGetter)),
            getBaseModelBakerConnected(modelState, baker, spriteGetter),
            getBaseModelBaker(modelState, baker, spriteGetter),
            getDiskModelBaker(DISK_MODEL, modelState, baker, spriteGetter),
            getDiskModelBaker(DISK_NEAR_CAPACITY_MODEL, modelState, baker, spriteGetter),
            getDiskModelBaker(DISK_FULL_MODEL, modelState, baker, spriteGetter),
            getDiskModelBaker(DISK_DISCONNECTED_MODEL, modelState, baker, spriteGetter),
            renderTypes
        );
    }

    private Function<Direction, BakedModel> getBaseModelBaker(final ModelState state,
                                                              final ModelBaker baker,
                                                              final Function<Material, TextureAtlasSprite> sg) {
        return direction -> {
            final Transformation rotation = new Transformation(null, RenderUtils.getQuaternion(direction), null, null);
            final ModelState wrappedState = new SimpleModelState(rotation, state.isUvLocked());
            return baker.bake(BASE_MODEL_DISCONNECTED, wrappedState, sg);
        };
    }

    private BiFunction<Direction, DyeColor, BakedModel> getBaseModelBakerConnected(final ModelState state,
                                                                                   final ModelBaker baker,
                                                                                   final Function<Material, TextureAtlasSprite> sg) {
        return (direction, color) -> {
            final Transformation rotation = new Transformation(null, RenderUtils.getQuaternion(direction), null, null);
            final ModelState wrappedState = new SimpleModelState(rotation, state.isUvLocked());
            return baker.bake(BASE_MODEL_CONNECTED.get(color), wrappedState, sg);
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
