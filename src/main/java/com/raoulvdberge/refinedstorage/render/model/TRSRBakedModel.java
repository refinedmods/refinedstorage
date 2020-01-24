package com.raoulvdberge.refinedstorage.render.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.TRSRTransformer;
import net.minecraftforge.common.model.TransformationHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class TRSRBakedModel implements IBakedModel {
    protected final IBakedModel original;
    protected TransformationMatrix transformation;
    private final int faceOffset;

    public TRSRBakedModel(IBakedModel original, float x, float y, float z, float scale) {
        this(original, x, y, z, 0, 0, 0, scale, scale, scale);
    }

    public TRSRBakedModel(IBakedModel original, float x, float y, float z, float rotX, float rotY, float rotZ, float scale) {
        this(original, x, y, z, rotX, rotY, rotZ, scale, scale, scale);
    }

    public TRSRBakedModel(IBakedModel original, float x, float y, float z, float rotX, float rotY, float rotZ, float scaleX, float scaleY, float scaleZ) {
        this(original, new TransformationMatrix(new Vector3f(x, y, z), null, new Vector3f(scaleX, scaleY, scaleZ), TransformationHelper.quatFromXYZ(new Vector3f(rotX, rotY, rotZ), true)));
    }

    public TRSRBakedModel(IBakedModel original, TransformationMatrix transform) {
        this.original = original;
        this.transformation = transform;
        this.faceOffset = 0;
    }

    public TRSRBakedModel(IBakedModel original, Direction facing) {
        this(original,facing,null);
    }

    public TRSRBakedModel(IBakedModel original, Direction facing, @Nullable Vector3f translation) {
        this.original = original;

        double r = Math.PI * (360 - facing.getOpposite().getHorizontalIndex() * 90) / 180d;

        this.transformation = new TransformationMatrix(translation, TransformationHelper.quatFromXYZ(new Vector3f(0, (float) r, 0), false), null, null);
        this.faceOffset = 4 + Direction.NORTH.getHorizontalIndex() - facing.getHorizontalIndex();
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
        ImmutableList.Builder<BakedQuad> quads = ImmutableList.builder();

        if (side != null && side.getHorizontalIndex() > -1) {
            side = Direction.byHorizontalIndex((side.getHorizontalIndex() + this.faceOffset) % 4);
        }

        for (BakedQuad quad : original.getQuads(state, side, rand, EmptyModelData.INSTANCE)) {
            BakedQuadBuilder builder = new BakedQuadBuilder(quad.func_187508_a());
            TRSRTransformer transformer = new TRSRTransformer(builder, transformation.blockCenterToCorner());

            quad.pipe(transformer);

            quads.add(builder.build());
        }

        return quads.build();
    }

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return original.isGui3d();
    }

    @Override
    public boolean func_230044_c_() {
        return original.func_230044_c_();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return original.isBuiltInRenderer();
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public TextureAtlasSprite getParticleTexture() {
        return original.getParticleTexture();
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public ItemCameraTransforms getItemCameraTransforms() {
        return original.getItemCameraTransforms();
    }

    @Nonnull
    @Override
    public ItemOverrideList getOverrides() {
        return original.getOverrides();
    }
}
