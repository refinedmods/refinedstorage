package com.raoulvdberge.refinedstorage.render.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.client.model.pipeline.VertexTransformer;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.annotation.Nonnull;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.List;
import java.util.Random;

/**
 * @link https://github.com/SlimeKnights/Mantle/blob/1.14/src/main/java/slimeknights/mantle/client/model/TRSRBakedModel.java
 */
// for those wondering TRSR stands for Translation Rotation Scale Rotation
public class TRSRBakedModel implements IBakedModel {
    protected final IBakedModel original;
    protected TRSRTransformation transformation;
    private final int faceOffset;

    public TRSRBakedModel(IBakedModel original, float x, float y, float z, float scale) {
        this(original, x, y, z, 0, 0, 0, scale, scale, scale);
    }

    public TRSRBakedModel(IBakedModel original, float x, float y, float z, float rotX, float rotY, float rotZ, float scale) {
        this(original, x, y, z, rotX, rotY, rotZ, scale, scale, scale);
    }

    public TRSRBakedModel(IBakedModel original, float x, float y, float z, float rotX, float rotY, float rotZ, float scaleX, float scaleY, float scaleZ) {
        this(original, new TRSRTransformation(new Vector3f(x, y, z),
            null,
            new Vector3f(scaleX, scaleY, scaleZ),
            TRSRTransformation.quatFromXYZ(rotX, rotY, rotZ)));
    }

    public TRSRBakedModel(IBakedModel original, TRSRTransformation transform) {
        this.original = original;
        this.transformation = TRSRTransformation.blockCenterToCorner(transform);
        this.faceOffset = 0;
    }

    /**
     * Rotates around the Y axis and adjusts culling appropriately. South is default.
     */
    public TRSRBakedModel(IBakedModel original, Direction facing) {
        this.original = original;

        this.faceOffset = 4 + Direction.NORTH.getHorizontalIndex() - facing.getHorizontalIndex();

        double r = Math.PI * (360 - facing.getOpposite().getHorizontalIndex() * 90) / 180d;
        TRSRTransformation t = new TRSRTransformation(null, null, null, TRSRTransformation.quatFromXYZ(0, (float) r, 0));
        this.transformation = TRSRTransformation.blockCenterToCorner(t);
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
        // transform quads obtained from parent

        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

        if (!original.isBuiltInRenderer()) {
            try {
                // adjust side to facing-rotation
                if (side != null && side.getHorizontalIndex() > -1) {
                    side = Direction.byHorizontalIndex((side.getHorizontalIndex() + faceOffset) % 4);
                }
                for (BakedQuad quad : original.getQuads(state, side, rand)) {
                    Transformer transformer = new Transformer(transformation, quad.getFormat());
                    quad.pipe(transformer);
                    builder.add(transformer.build());
                }
            } catch (Exception e) {
                // do nothing. Seriously, why are you using immutable lists?!
            }
        }

        return builder.build();
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

    private static class Transformer extends VertexTransformer {
        protected Matrix4f transformation;
        protected Matrix3f normalTransformation;

        public Transformer(TRSRTransformation transformation, VertexFormat format) {
            super(new UnpackedBakedQuad.Builder(format));
            // position transform
            this.transformation = transformation.getMatrixVec();
            // normal transform
            this.normalTransformation = new Matrix3f();
            this.transformation.getRotationScale(this.normalTransformation);
            this.normalTransformation.invert();
            this.normalTransformation.transpose();
        }

        @Override
        public void put(int element, float... data) {
            VertexFormatElement.Usage usage = parent.getVertexFormat().getElement(element).getUsage();

            // transform normals and position
            if (usage == VertexFormatElement.Usage.POSITION && data.length >= 3) {
                Vector4f vec = new Vector4f(data[0], data[1], data[2], 1f);
                transformation.transform(vec);
                data = new float[4];
                vec.get(data);
            } else if (usage == VertexFormatElement.Usage.NORMAL && data.length >= 3) {
                Vector3f vec = new Vector3f(data);
                normalTransformation.transform(vec);
                vec.normalize();
                data = new float[4];
                vec.get(data);
            }
            super.put(element, data);
        }

        public UnpackedBakedQuad build() {
            return ((UnpackedBakedQuad.Builder) parent).build();
        }
    }
}
