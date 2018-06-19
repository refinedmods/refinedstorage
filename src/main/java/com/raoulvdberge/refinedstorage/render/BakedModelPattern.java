package com.raoulvdberge.refinedstorage.render;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternRenderHandler;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.CraftingPattern;
import com.raoulvdberge.refinedstorage.item.ItemPattern;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
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
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;

public class BakedModelPattern implements IBakedModel {
    private IBakedModel base;

    public BakedModelPattern(IBakedModel base) {
        this.base = base;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        TRSRTransformation transform = RenderUtils.getDefaultItemTransforms().get(cameraTransformType);

        return Pair.of(this, transform == null ? RenderUtils.EMPTY_MATRIX_TRANSFORM : transform.getMatrix());
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return base.getQuads(state, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return base.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return base.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return base.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return base.getParticleTexture();
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemCameraTransforms getItemCameraTransforms() {
        return base.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return new ItemOverrideList(base.getOverrides().getOverrides()) {
            @Override
            public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
                CraftingPattern pattern = ItemPattern.getPatternFromCache(world, stack);

                if (canDisplayPatternOutput(stack, pattern)) {
                    return Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(pattern.getOutputs().get(0), world, entity);
                }

                return super.handleItemState(originalModel, stack, world, entity);
            }
        };
    }

    public static boolean canDisplayPatternOutput(ItemStack patternStack, CraftingPattern pattern) {
        if (pattern.isValid() && pattern.getOutputs().size() == 1) {
            for (ICraftingPatternRenderHandler renderHandler : API.instance().getPatternRenderHandlers()) {
                if (renderHandler.canRenderOutput(patternStack)) {
                    return true;
                }
            }
        }

        return false;
    }
}
