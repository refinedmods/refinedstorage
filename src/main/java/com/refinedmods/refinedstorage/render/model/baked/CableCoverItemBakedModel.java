package com.refinedmods.refinedstorage.render.model.baked;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.refinedmods.refinedstorage.render.model.baked.CableCoverBakedModel.addCover;

public class CableCoverItemBakedModel implements BakedModel {
    private static final LoadingCache<CacheKey, List<BakedQuad>> CACHE = CacheBuilder.newBuilder().build(new CacheLoader<CacheKey, List<BakedQuad>>() {
        @Override
        public List<BakedQuad> load(CacheKey key) {
            List<BakedQuad> quads = new ArrayList<>();
            addCover(quads, new Cover(key.stack, key.type), Direction.NORTH, key.side, key.random, null, null, true);
            return quads;
        }
    });
    private final ItemStack stack;
    private final CoverType type;

    public CableCoverItemBakedModel(ItemStack stack, CoverType type) {
        this.stack = stack;
        this.type = type;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        if (stack.isEmpty()) {
            return Collections.emptyList();
        }
        CacheKey key = new CacheKey(state, CoverItem.getItem(stack), side, type, rand);
        return CACHE.getUnchecked(key);
    }

    @Override
    public ItemOverrides getOverrides() {
        return new ItemOverrides() {
            @Override
            public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int p) {
                return new CableCoverItemBakedModel(stack, type);
            }
        };
    }

    @Override
    public List<BakedModel> getRenderPasses(final ItemStack itemStack, final boolean fabulous) {
        return List.of(this);
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
        return RenderUtils.getDefaultBlockTransforms();
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    private static class CacheKey {
        private final BlockState state;
        private final ItemStack stack;
        private final Direction side;
        private final CoverType type;
        private final RandomSource random;

        CacheKey(BlockState state, ItemStack stack, Direction side, CoverType type, RandomSource rand) {
            this.state = state;
            this.stack = stack;
            this.side = side;
            this.type = type;
            this.random = rand;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            CableCoverItemBakedModel.CacheKey cacheKey = (CableCoverItemBakedModel.CacheKey) o;

            return cacheKey.type == type && cacheKey.stack.getItem() == stack.getItem() && cacheKey.side == side && Objects.equals(cacheKey.state, state);
        }

        @Override
        public int hashCode() {
            int result = stack.getItem().hashCode();
            result = 31 * result + (side != null ? side.hashCode() : 0);
            result = 31 * result + (state != null ? state.hashCode() : 0);
            result = 31 * result + type.hashCode();
            return result;
        }
    }
}
