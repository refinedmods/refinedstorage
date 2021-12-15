package com.refinedmods.refinedstorage.render.model;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Transformation;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.Cover;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.CoverType;
import com.refinedmods.refinedstorage.item.CoverItem;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.*;

public class BakedModelCover extends BakedModelCableCover {

    private static final LoadingCache<CacheKey, List<BakedQuad>> CACHE = CacheBuilder.newBuilder().build(new CacheLoader<CacheKey, List<BakedQuad>>() {
        @Override
        public List<BakedQuad> load(CacheKey key) {
            List<BakedQuad> quads = new ArrayList<>();

            addCover(quads, new Cover(key.stack, key.type), Direction.NORTH, key.side, new Random(), null, null, true);

            return quads;
        }
    });
    private ItemStack stack;
    private CoverType type;

    public BakedModelCover(ItemStack stack, CoverType type) {
        super(null);

        this.stack = stack;
        this.type = type;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
        if (stack.isEmpty()) {
            return Collections.emptyList();
        }

        CacheKey key = new CacheKey(state, CoverItem.getItem(stack), side, type);

        return CACHE.getUnchecked(key);
    }

    @Override
    public ItemOverrides getOverrides() {
        return new ItemOverrides() {
            @Override
            public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int p) {
                return new BakedModelCover(stack, type);
            }
        };
    }

    @Override
    public BakedModel handlePerspective(ItemTransforms.TransformType cameraTransformType, PoseStack poseStack) {
        Transformation transform = RenderUtils.getDefaultBlockTransforms().get(cameraTransformType);
        if (transform != null) transform.push(poseStack);
        return this;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return null;
    }

    @Override
    public boolean useAmbientOcclusion(BlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemTransforms getTransforms() {
        return ItemTransforms.NO_TRANSFORMS;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    private class CacheKey {
        private BlockState state;
        private ItemStack stack;
        private Direction side;
        private CoverType type;

        CacheKey(BlockState state, ItemStack stack, Direction side, CoverType type) {
            this.state = state;
            this.stack = stack;
            this.side = side;
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            BakedModelCover.CacheKey cacheKey = (BakedModelCover.CacheKey) o;

            return cacheKey.type == type && cacheKey.stack.getItem() == stack.getItem() /* && cacheKey.stack.getItemDamage() == stack.getItemDamage()*/ && cacheKey.side == side && Objects.equals(cacheKey.state, state);
        }

        @Override
        public int hashCode() {
            int result = stack.getItem().hashCode();
            //result = 31 * result + stack.getDamage();
            result = 31 * result + (side != null ? side.hashCode() : 0);
            result = 31 * result + (state != null ? state.hashCode() : 0);
            result = 31 * result + type.hashCode();
            return result;
        }
    }
}
