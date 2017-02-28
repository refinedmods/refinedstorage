package com.raoulvdberge.refinedstorage.render;

import com.google.common.collect.ImmutableMap;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.CraftingPattern;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.item.ItemPattern;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;

public class BakedModelPattern implements IBakedModel, IPerspectiveAwareModel {
    private IBakedModel patternModel;

    private static TRSRTransformation get(float tx, float ty, float tz, float ax, float ay, float az, float s) {
        return new TRSRTransformation(
                new javax.vecmath.Vector3f(tx / 16, ty / 16, tz / 16),
                TRSRTransformation.quatFromXYZDegrees(new javax.vecmath.Vector3f(ax, ay, az)),
                new javax.vecmath.Vector3f(s, s, s),
                null);
    }

    private static ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms =
            ImmutableMap.<ItemCameraTransforms.TransformType, TRSRTransformation>builder()
                    .put(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND,     get(0, 3, 1, 0, 0, 0, 0.55f))
                    .put(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND,      get(0, 3, 1, 0, 0, 0, 0.55f))
                    .put(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND,     get(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f))
                    .put(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND,      get(1.13f, 3.2f, 1.13f, 0, 90, -25, 0.68f))
                    .put(ItemCameraTransforms.TransformType.GROUND,                      get(0, 2, 0, 0, 0, 0, 0.5f))
                    .put(ItemCameraTransforms.TransformType.HEAD,                        get(0, 13, 7, 0, 180, 0, 1)).build();

    public BakedModelPattern(IBakedModel patternModel) {
        this.patternModel = patternModel;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return patternModel.getQuads(state, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return patternModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return patternModel.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return patternModel.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return patternModel.getParticleTexture();
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemCameraTransforms getItemCameraTransforms() {
        return patternModel.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return new ItemOverrideList(patternModel.getOverrides().getOverrides()) {
            @Override
            public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
                CraftingPattern pattern = ItemPattern.getPatternFromCache(world, stack);

                if (GuiBase.isShiftKeyDown() && pattern.isValid() && pattern.getOutputs().size() == 1) {
                    return Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(pattern.getOutputs().get(0), world, entity);
                }

                return super.handleItemState(originalModel, stack, world, entity);
            }
        };
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        return Pair.of(this,
                transforms.get(cameraTransformType) != null ?
                        transforms.get(cameraTransformType).getMatrix() : get(0, 0, 0, 0, 0, 0, 1.0f).getMatrix());
    }
}
