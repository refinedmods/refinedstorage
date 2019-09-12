package com.raoulvdberge.refinedstorage.render.model.baked;
/*
import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingPatternRenderHandler;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.apiimpl.autocrafting.CraftingPattern;
import com.raoulvdberge.refinedstorage.item.ItemPattern;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;

public class BakedModelPattern extends BakedModelDelegate {
    public BakedModelPattern(IBakedModel base) {
        super(base);
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        TRSRTransformation transform = RenderUtils.getDefaultItemTransforms().get(cameraTransformType);

        return Pair.of(this, transform == null ? RenderUtils.EMPTY_MATRIX_TRANSFORM : transform.getMatrix());
    }

    @Override
    public ItemOverrideList getOverrides() {
        return new ItemOverrideList(base.getOverrides().getOverrides()) {
            @Override
            public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
                CraftingPattern pattern = ItemPattern.getPatternFromCache(world, stack);

                if (canDisplayOutput(stack, pattern)) {
                    ItemStack outputToRender = pattern.getOutputs().get(0);

                    // @Volatile: Gregtech banned for rendering due to issues
                    if (!hasBrokenRendering(outputToRender)) {
                        return Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(outputToRender, world, entity);
                    }
                }

                return super.handleItemState(originalModel, stack, world, entity);
            }
        };
    }

    private boolean hasBrokenRendering(ItemStack stack) {
        if ("gregtech".equals(stack.getItem().getCreatorModId(stack))) {
            if ("tile.pipe".equals(stack.getTranslationKey())) {
                return true;
            }

            if ("machine".equals(stack.getItem().delegate.name().getPath())) {
                return true;
            }
        }
        return false;
    }

    public static boolean canDisplayOutput(ItemStack patternStack, CraftingPattern pattern) {
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
*/