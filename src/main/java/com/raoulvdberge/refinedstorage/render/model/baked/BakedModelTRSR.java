package com.raoulvdberge.refinedstorage.render.model.baked;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.client.model.pipeline.VertexTransformer;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.ArrayList;
import java.util.List;

/**
 * @link https://github.com/SlimeKnights/Mantle/blob/master/src/main/java/slimeknights/mantle/client/model/TRSRBakedModel.java
 */
public class BakedModelTRSR implements IBakedModel {
    protected final IBakedModel original;
    public TRSRTransformation transformation;
    private final TRSROverride override;
    private final int faceOffset;

    public BakedModelTRSR(IBakedModel original, float x, float y, float z, float scale) {
        this(original, x, y, z, 0, 0, 0, scale, scale, scale);
    }

    public BakedModelTRSR(IBakedModel original, float x, float y, float z, float rotX, float rotY, float rotZ, float scale) {
        this(original, x, y, z, rotX, rotY, rotZ, scale, scale, scale);
    }

    public BakedModelTRSR(IBakedModel original, float x, float y, float z, float rotX, float rotY, float rotZ, float scaleX, float scaleY, float scaleZ) {
        this(original, new TRSRTransformation(new Vector3f(x, y, z),
            null,
            new Vector3f(scaleX, scaleY, scaleZ),
            TRSRTransformation.quatFromXYZ(rotX, rotY, rotZ)));
    }

    public BakedModelTRSR(IBakedModel original, TRSRTransformation transform) {
        this.original = original;
        this.transformation = TRSRTransformation.blockCenterToCorner(transform);
        this.override = new TRSROverride(this);
        this.faceOffset = 0;
    }

    /**
     * Rotates around the Y axis and adjusts culling appropriately. South is default.
     */
    public BakedModelTRSR(IBakedModel original, EnumFacing facing) {
        this.original = original;
        this.override = new TRSROverride(this);

        this.faceOffset = 4 + EnumFacing.NORTH.getHorizontalIndex() - facing.getHorizontalIndex();

        double r = Math.PI * (360 - facing.getOpposite().getHorizontalIndex() * 90) / 180d;
        TRSRTransformation t = new TRSRTransformation(null, null, null, TRSRTransformation.quatFromXYZ(0, (float) r, 0));
        this.transformation = TRSRTransformation.blockCenterToCorner(t);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        // transform quads obtained from parent

        List<BakedQuad> quads = new ArrayList<>();

        if (!original.isBuiltInRenderer()) {
            try {
                // adjust side to facing-rotation
                if (side != null && side.getHorizontalIndex() > -1) {
                    side = EnumFacing.byHorizontalIndex((side.getHorizontalIndex() + faceOffset) % 4);
                }
                for (BakedQuad quad : original.getQuads(state, side, rand)) {
                    Transformer transformer = new Transformer(transformation, quad.getFormat());
                    quad.pipe(transformer);
                    quads.add(transformer.build());
                }
            } catch (Exception e) {
                // do nothing. Seriously, why are you using immutable lists?!
            }
        }

        return quads;
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
        return override;
    }

    private static class TRSROverride extends ItemOverrideList {
        private final BakedModelTRSR model;

        public TRSROverride(BakedModelTRSR model) {
            super(ImmutableList.of());

            this.model = model;
        }

        @Nonnull
        @Override
        public IBakedModel handleItemState(@Nonnull IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
            IBakedModel baked = model.original.getOverrides().handleItemState(originalModel, stack, world, entity);

            return new BakedModelTRSR(baked, model.transformation);
        }
    }

    private static class Transformer extends VertexTransformer {
        protected Matrix4f transformation;
        protected Matrix3f normalTransformation;

        public Transformer(TRSRTransformation transformation, VertexFormat format) {
            super(new UnpackedBakedQuad.Builder(format));
            // position transform
            this.transformation = transformation.getMatrix();
            // normal transform
            this.normalTransformation = new Matrix3f();
            this.transformation.getRotationScale(this.normalTransformation);
            this.normalTransformation.invert();
            this.normalTransformation.transpose();
        }

        @Override
        public void put(int element, float... data) {
            VertexFormatElement.EnumUsage usage = parent.getVertexFormat().getElement(element).getUsage();

            // transform normals and position
            if (usage == VertexFormatElement.EnumUsage.POSITION && data.length >= 3) {
                Vector4f vec = new Vector4f(data[0], data[1], data[2], 1f);
                transformation.transform(vec);
                data = new float[4];
                vec.get(data);
            } else if (usage == VertexFormatElement.EnumUsage.NORMAL && data.length >= 3) {
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