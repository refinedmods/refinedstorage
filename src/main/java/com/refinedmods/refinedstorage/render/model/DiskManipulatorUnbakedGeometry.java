package com.refinedmods.refinedstorage.render.model;

import com.mojang.math.Transformation;
import com.mojang.math.Vector3f;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.render.model.baked.DiskManipulatorBakedModel;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DiskManipulatorUnbakedGeometry extends AbstractUnbakedGeometry<DiskManipulatorUnbakedGeometry> {
    private static final ResourceLocation BASE_MODEL_DISCONNECTED = new ResourceLocation(RS.ID, "block/disk_manipulator/disconnected");

    private final Map<DyeColor, ResourceLocation> BASE_MODEL_CONNECTED = new HashMap<>();
    private static final ResourceLocation DISK_MODEL = new ResourceLocation(RS.ID, "block/disks/disk");
    private static final ResourceLocation DISK_DISCONNECTED_MODEL = new ResourceLocation(RS.ID, "block/disks/disk_disconnected");
    private static final ResourceLocation DISK_FULL_MODEL = new ResourceLocation(RS.ID, "block/disks/disk_full");
    private static final ResourceLocation DISK_NEAR_CAPACITY_MODEL = new ResourceLocation(RS.ID, "block/disks/disk_near_capacity");

    @Override
    protected Set<ResourceLocation> getModels() {
        Set<ResourceLocation> set = new HashSet<>(Set.of(
            BASE_MODEL_DISCONNECTED,
            DISK_MODEL,
            DISK_DISCONNECTED_MODEL,
            DISK_FULL_MODEL,
            DISK_NEAR_CAPACITY_MODEL
        ));
        set.addAll(BASE_MODEL_CONNECTED.values());
        return set;
    }

    public DiskManipulatorUnbakedGeometry() {
        for (DyeColor value : DyeColor.values()) {
            BASE_MODEL_CONNECTED.put(value, new ResourceLocation(RS.ID, "block/disk_manipulator/" + value.getName()));
        }
    }

    @Override
    public BakedModel bake(final IGeometryBakingContext context,
                           final ModelBakery bakery,
                           final Function<Material, TextureAtlasSprite> spriteGetter,
                           final ModelState modelState,
                           final ItemOverrides overrides,
                           final ResourceLocation modelLocation) {
        var renderTypeHint = context.getRenderTypeHint();
        var renderTypes = renderTypeHint != null ? context.getRenderType(renderTypeHint) : RenderTypeGroup.EMPTY;

        return new DiskManipulatorBakedModel(
            Objects.requireNonNull(bakery.bake(BASE_MODEL_DISCONNECTED, modelState, spriteGetter)),
            getBaseModelBakeryConnected(modelState, bakery, spriteGetter),
            getBaseModelBakery(modelState, bakery, spriteGetter),
            getDiskModelBakery(DISK_MODEL, modelState, bakery, spriteGetter),
            getDiskModelBakery(DISK_NEAR_CAPACITY_MODEL, modelState, bakery, spriteGetter),
            getDiskModelBakery(DISK_FULL_MODEL, modelState, bakery, spriteGetter),
            getDiskModelBakery(DISK_DISCONNECTED_MODEL, modelState, bakery, spriteGetter),
            renderTypes
        );
    }

    private Function<Direction, BakedModel> getBaseModelBakery(final ModelState state,
                                                               final ModelBakery bakery,
                                                               final Function<Material, TextureAtlasSprite> sg) {
        return direction -> {
            var dir = RenderUtils.getQuaternion(direction);
            final Transformation rotation = new Transformation(null, dir, null, null);
            final ModelState wrappedState = new SimpleModelState(rotation, state.isUvLocked());
            return bakery.bake(BASE_MODEL_DISCONNECTED, wrappedState, sg);
        };
    }

    private BiFunction<Direction, DyeColor, BakedModel> getBaseModelBakeryConnected(final ModelState state,
                                                                                    final ModelBakery bakery,
                                                                                    final Function<Material, TextureAtlasSprite> sg) {
        return (direction, color) -> {
            var dir = RenderUtils.getQuaternion(direction);
            final Transformation rotation = new Transformation(null, dir, null, null);
            final ModelState wrappedState = new SimpleModelState(rotation, state.isUvLocked());
            return bakery.bake(BASE_MODEL_CONNECTED.get(color), wrappedState, sg);
        };
    }

    private BiFunction<Direction, Vector3f, BakedModel> getDiskModelBakery(final ResourceLocation id,
                                                                           final ModelState state,
                                                                           final ModelBakery bakery,
                                                                           final Function
                                                                               <Material, TextureAtlasSprite> sg) {
        return (direction, trans) -> {
            var dir = RenderUtils.getQuaternion(direction);
            final Transformation translation = new Transformation(trans, null, null, null);
            final Transformation rotation = new Transformation(null, dir, null, null);
            final ModelState wrappedState = new SimpleModelState(rotation.compose(translation), state.isUvLocked());
            return bakery.bake(id, wrappedState, sg);
        };
    }
}
