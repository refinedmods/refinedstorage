package com.raoulvdberge.refinedstorage.render.model;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.client.model.pipeline.VertexLighterFlat;
import net.minecraftforge.common.model.TransformationHelper;

import javax.annotation.Nullable;
import java.util.*;

public class FullbrightBakedModel extends DelegateBakedModel {
    private static final LoadingCache<CacheKey, List<BakedQuad>> CACHE = CacheBuilder.newBuilder().build(new CacheLoader<CacheKey, List<BakedQuad>>() {
        @Override
        public List<BakedQuad> load(CacheKey key) {
            return transformQuads(key.base.getQuads(key.state, key.side, key.random, EmptyModelData.INSTANCE), key.textures);
        }
    });

    private Set<ResourceLocation> textures;
    private boolean cacheDisabled = false;

    public FullbrightBakedModel(IBakedModel base, ResourceLocation... textures) {
        super(base);

        this.textures = new HashSet<>(Arrays.asList(textures));
    }

    public FullbrightBakedModel disableCache() {
        this.cacheDisabled = true;

        return this;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand, IModelData data) {
        if (state == null) {
            return base.getQuads(state, side, rand, data);
        }

        if (cacheDisabled) {
            return transformQuads(base.getQuads(state, side, rand, data), textures);
        }

        return CACHE.getUnchecked(new CacheKey(base, textures, rand, state, side));
    }

    private static List<BakedQuad> transformQuads(List<BakedQuad> oldQuads, Set<ResourceLocation> textures) {
        List<BakedQuad> quads = new ArrayList<>(oldQuads);

        for (int i = 0; i < quads.size(); ++i) {
            BakedQuad quad = quads.get(i);

            if (textures.contains(quad.getSprite().getName())) {
                quads.set(i, transformQuad(quad, 0.007F));
            }
        }

        return quads;
    }

    private static BakedQuad transformQuad(BakedQuad quad, float light) {
        if (RenderUtils.isLightMapDisabled()) {
            return quad;
        }

        VertexFormat newFormat = RenderUtils.getFormatWithLightMap(quad.getFormat());

        UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(newFormat);

        VertexLighterFlat trans = new VertexLighterFlat(Minecraft.getInstance().getBlockColors()) {
            @Override
            protected void updateLightmap(float[] normal, float[] lightmap, float x, float y, float z) {
                lightmap[0] = light;
                lightmap[1] = light;
            }

            @Override
            public void setQuadTint(int tint) {
                // NO OP
            }
        };

        trans.setParent(builder);

        // TODO quad.pipe(trans);

        builder.setQuadTint(quad.getTintIndex());
        builder.setQuadOrientation(quad.getFace());
        builder.setTexture(quad.getSprite());
        builder.setApplyDiffuseLighting(false);

        return builder.build();
    }

    private class CacheKey {
        private IBakedModel base;
        private Set<ResourceLocation> textures;
        private Random random;
        private BlockState state;
        private Direction side;

        public CacheKey(IBakedModel base, Set<ResourceLocation> textures, Random random, BlockState state, Direction side) {
            this.base = base;
            this.textures = textures;
            this.random = random;
            this.state = state;
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

            CacheKey cacheKey = (CacheKey) o;

            if (cacheKey.side != side) {
                return false;
            }

            if (!state.equals(cacheKey.state)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            return state.hashCode() + (31 * (side != null ? side.hashCode() : 0));
        }
    }
}
