package com.refinedmods.refinedstorage.render.model;

import com.mojang.math.Transformation;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.render.model.baked.PortableGridBakedModel;
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
import java.util.function.Function;

public class PortableGridUnbakedGeometry extends AbstractUnbakedGeometry<PortableGridUnbakedGeometry> {
    private static final ResourceLocation BASE_CONNECTED_MODEL = new ResourceLocation(RS.ID, "block/portable_grid_connected");
    private static final ResourceLocation BASE_DISCONNECTED_MODEL = new ResourceLocation(RS.ID, "block/portable_grid_disconnected");
    private static final ResourceLocation DISK_MODEL = new ResourceLocation(RS.ID, "block/disks/portable_grid_disk");
    private static final ResourceLocation DISK_DISCONNECTED_MODEL = new ResourceLocation(RS.ID, "block/disks/portable_grid_disk_disconnected");
    private static final ResourceLocation DISK_FULL_MODEL = new ResourceLocation(RS.ID, "block/disks/portable_grid_disk_full");
    private static final ResourceLocation DISK_NEAR_CAPACITY_MODEL = new ResourceLocation(RS.ID, "block/disks/portable_grid_disk_near_capacity");

    @Override
    protected Set<ResourceLocation> getModels() {
        return Set.of(
            BASE_CONNECTED_MODEL,
            BASE_DISCONNECTED_MODEL,
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
        return new PortableGridBakedModel(
            Objects.requireNonNull(bakery.bake(BASE_CONNECTED_MODEL, modelState, spriteGetter)),
            getModelBakery(BASE_CONNECTED_MODEL, modelState, bakery, spriteGetter),
            getModelBakery(BASE_DISCONNECTED_MODEL, modelState, bakery, spriteGetter),
            getModelBakery(DISK_MODEL, modelState, bakery, spriteGetter),
            getModelBakery(DISK_NEAR_CAPACITY_MODEL, modelState, bakery, spriteGetter),
            getModelBakery(DISK_FULL_MODEL, modelState, bakery, spriteGetter),
            getModelBakery(DISK_DISCONNECTED_MODEL, modelState, bakery, spriteGetter)
        );
    }

    private Function<Direction, BakedModel> getModelBakery(final ResourceLocation id,
                                                           final ModelState state,
                                                           final ModelBakery bakery,
                                                           final Function<Material, TextureAtlasSprite> sg) {
        return direction -> {
            final Transformation rotation = new Transformation(null, direction.getRotation(), null, null);
            final ModelState wrappedState = new SimpleModelState(rotation, state.isUvLocked());
            return bakery.bake(id, wrappedState, sg);
        };
    }
}