package com.refinedmods.refinedstorage.render.model.baked;

import com.google.common.collect.ImmutableList;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternRenderHandler;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.item.PatternItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import javax.annotation.Nullable;

public class PatternBakedModel extends BakedModelWrapper<BakedModel> {
    public PatternBakedModel(BakedModel base) {
        super(base);
    }

    public static boolean canDisplayOutput(ItemStack patternStack, ICraftingPattern pattern) {
        if (pattern.isValid() && pattern.getOutputs().size() == 1) {
            for (ICraftingPatternRenderHandler renderHandler : API.instance().getPatternRenderHandlers()) {
                if (renderHandler.canRenderOutput(patternStack)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public ItemOverrides getOverrides() {
        return new ItemOverrides() {
            @Nullable
            @Override
            public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int p) {
                if (entity != null) {
                    ICraftingPattern pattern = PatternItem.fromCache(entity.level(), stack);

                    if (canDisplayOutput(stack, pattern)) {
                        ItemStack outputToRender = pattern.getOutputs().get(0);

                        return Minecraft.getInstance().getItemRenderer().getModel(outputToRender, level, entity, p);
                    }
                }

                return super.resolve(model, stack, level, entity, p);
            }

            @Override
            public ImmutableList<BakedOverride> getOverrides() {
                return originalModel.getOverrides().getOverrides();
            }
        };
    }
}
