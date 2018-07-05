package com.raoulvdberge.refinedstorage.render.model.baked;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.raoulvdberge.refinedstorage.item.ItemCover;
import com.raoulvdberge.refinedstorage.render.CubeBuilder;
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
import net.minecraftforge.client.ForgeHooksClient;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class BakedModelCover implements IBakedModel {
    private class CacheKey {
        private IBakedModel base;
        private IBlockState state;
        private ItemStack stack;
        private EnumFacing side;

        CacheKey(IBakedModel base, IBlockState state, ItemStack stack, EnumFacing side) {
            this.base = base;
            this.state = state;
            this.stack = stack;
            this.side = side;
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

            return cacheKey.stack.getItem() == stack.getItem() && cacheKey.stack.getItemDamage() == stack.getItemDamage() && cacheKey.side == side && Objects.equals(cacheKey.state, state);
        }

        @Override
        public int hashCode() {
            int result = stack.getItem().hashCode();
            result = 31 * result + stack.getItemDamage();
            result = 31 * result + (side != null ? side.hashCode() : 0);
            result = 31 * result + (state != null ? state.hashCode() : 0);
            return result;
        }
    }

    private static final LoadingCache<CacheKey, List<BakedQuad>> CACHE = CacheBuilder.newBuilder().build(new CacheLoader<CacheKey, List<BakedQuad>>() {
        @Override
        public List<BakedQuad> load(CacheKey key) {
            List<BakedQuad> quads = new ArrayList<>(key.base.getQuads(key.state, key.side, 0));

            TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();

            if (!key.stack.isEmpty()) {
                IBlockState coverState = CoverManager.getBlockState(key.stack);

                if (coverState != null) {
                    IBakedModel coverModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(coverState);

                    sprite = BakedModelCableCover.getSprite(coverModel, coverState, key.side, 0);
                }
            }

            quads.addAll(new CubeBuilder()
                .from(0, 0, 0)
                .to(16, 16, 2)

                .face(EnumFacing.NORTH, 0, 16, 0, 16, sprite)
                .face(EnumFacing.SOUTH, 0, 16, 0, 16, sprite)

                .face(EnumFacing.UP, 0, 16, 0, 2, sprite)
                .face(EnumFacing.DOWN, 0, 16, 14, 16, sprite)
                .face(EnumFacing.EAST, 14, 16, 0, 16, sprite)
                .face(EnumFacing.WEST, 0, 2, 0, 16, sprite)

                .bake()
            );

            return quads;
        }
    });

    @Nullable
    private ItemStack stack;
    private IBakedModel base;

    public BakedModelCover(IBakedModel base, @Nullable ItemStack stack) {
        this.base = base;
        this.stack = stack;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        if (stack == null) {
            return Collections.emptyList();
        }

        CacheKey key = new CacheKey(base, state, ItemCover.getItem(stack), side);

        return CACHE.getUnchecked(key);
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemCameraTransforms getItemCameraTransforms() {
        return base.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        if (stack != null) {
            return base.getOverrides();
        }

        return new ItemOverrideList(Collections.emptyList()) {
            @Override
            public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
                return new BakedModelCover(base, stack);
            }
        };
    }

    @Override
    public boolean isAmbientOcclusion(IBlockState state) {
        return base.isAmbientOcclusion(state);
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        Pair<? extends IBakedModel, Matrix4f> matrix = base.handlePerspective(cameraTransformType);
        Pair<? extends IBakedModel, Matrix4f> bakedModel = ForgeHooksClient.handlePerspective(this, cameraTransformType);

        return Pair.of(bakedModel.getKey(), matrix.getRight());
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
}
