package com.refinedmods.refinedstorage.render.model;

import com.mojang.math.Transformation;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.render.model.baked.PortableGridBakedModel;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.SimpleModelState;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import java.util.Objects;
import java.util.function.Function;

public class PortableGridUnbakedGeometry implements IUnbakedGeometry<PortableGridUnbakedGeometry> {
    private static final ResourceLocation BASE_CONNECTED_MODEL = new ResourceLocation(RS.ID, "block/portable_grid_connected");
    private static final ResourceLocation BASE_DISCONNECTED_MODEL = new ResourceLocation(RS.ID, "block/portable_grid_disconnected");
    private static final ResourceLocation DISK_MODEL = new ResourceLocation(RS.ID, "block/disks/portable_grid_disk");
    private static final ResourceLocation DISK_DISCONNECTED_MODEL = new ResourceLocation(RS.ID, "block/disks/portable_grid_disk_disconnected");
    private static final ResourceLocation DISK_FULL_MODEL = new ResourceLocation(RS.ID, "block/disks/portable_grid_disk_full");
    private static final ResourceLocation DISK_NEAR_CAPACITY_MODEL = new ResourceLocation(RS.ID, "block/disks/portable_grid_disk_near_capacity");

    @Override
    public void resolveParents(final Function<ResourceLocation, UnbakedModel> modelGetter,
                               final IGeometryBakingContext context) {
        modelGetter.apply(BASE_CONNECTED_MODEL).resolveParents(modelGetter);
        modelGetter.apply(BASE_DISCONNECTED_MODEL).resolveParents(modelGetter);
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

        var renderTypeHint = context.getRenderTypeHint();
        var renderTypes = renderTypeHint != null ? context.getRenderType(renderTypeHint) : RenderTypeGroup.EMPTY;

        return new PortableGridBakedModel(
            Objects.requireNonNull(baker.bake(BASE_CONNECTED_MODEL, modelState, spriteGetter)),
            getModelBaker(BASE_CONNECTED_MODEL, modelState, baker, spriteGetter),
            getModelBaker(BASE_DISCONNECTED_MODEL, modelState, baker, spriteGetter),
            getModelBaker(DISK_MODEL, modelState, baker, spriteGetter),
            getModelBaker(DISK_NEAR_CAPACITY_MODEL, modelState, baker, spriteGetter),
            getModelBaker(DISK_FULL_MODEL, modelState, baker, spriteGetter),
            getModelBaker(DISK_DISCONNECTED_MODEL, modelState, baker, spriteGetter),
            renderTypes
        );
    }

    private Function<Direction, BakedModel> getModelBaker(final ResourceLocation id,
                                                          final ModelState state,
                                                          final ModelBaker baker,
                                                          final Function<Material, TextureAtlasSprite> sg) {
        return direction -> {
            final Transformation rotation = new Transformation(null, RenderUtils.getQuaternion(direction), null, null);
            final ModelState wrappedState = new SimpleModelState(rotation, state.isUvLocked());
            return baker.bake(id, wrappedState, sg);
        };
    }
}