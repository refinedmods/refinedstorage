package com.raoulvdberge.refinedstorage.render.model.baked;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.raoulvdberge.refinedstorage.block.BlockCable;
import com.raoulvdberge.refinedstorage.render.QuadBuilder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;

public class BakedModelCableCover implements IBakedModel {
    private IBakedModel base;
    private TextureAtlasSprite genericGreyTexture;

    public BakedModelCableCover(IBakedModel base) {
        this.base = base;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        List<BakedQuad> quads = new ArrayList<>(base.getQuads(state, side, rand));

        if (state != null) {
            IExtendedBlockState s = (IExtendedBlockState) state;

            addCover(quads, s.getValue(BlockCable.COVER_NORTH), EnumFacing.NORTH, side, rand, s);
            addCover(quads, s.getValue(BlockCable.COVER_SOUTH), EnumFacing.SOUTH, side, rand, s);
            addCover(quads, s.getValue(BlockCable.COVER_EAST), EnumFacing.EAST, side, rand, s);
            addCover(quads, s.getValue(BlockCable.COVER_WEST), EnumFacing.WEST, side, rand, s);
            addCover(quads, s.getValue(BlockCable.COVER_DOWN), EnumFacing.DOWN, side, rand, s);
            addCover(quads, s.getValue(BlockCable.COVER_UP), EnumFacing.UP, side, rand, s);
        }

        return quads;
    }

    private void addCover(List<BakedQuad> quads, @Nullable ItemStack coverStack, EnumFacing coverSide, EnumFacing side, long rand, IExtendedBlockState state) {
        if (coverStack == null) {
            return;
        }

        IBlockState coverState = CoverManager.getBlockState(coverStack);

        if (coverState == null) {
            return;
        }

        IBakedModel coverModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(coverState);

        TextureAtlasSprite sprite = getSprite(coverModel, coverState, side, rand);

        boolean hasUp = CoverManager.getBlockState(state.getValue(BlockCable.COVER_UP)) != null;
        boolean hasDown = CoverManager.getBlockState(state.getValue(BlockCable.COVER_DOWN)) != null;

        boolean hasEast = CoverManager.getBlockState(state.getValue(BlockCable.COVER_EAST)) != null;
        boolean hasWest = CoverManager.getBlockState(state.getValue(BlockCable.COVER_WEST)) != null;

        float handleAngle = 0;
        EnumFacing.Axis handleAxis = EnumFacing.Axis.Y;

        if (coverSide == EnumFacing.NORTH) {
            quads.addAll(QuadBuilder.withFormat(DefaultVertexFormats.ITEM)
                .setFrom(hasWest ? 2 : 0, hasDown ? 2 : 0, 0)
                .setTo(hasEast ? 14 : 16, hasUp ? 14 : 16, 2)

                .addFace(EnumFacing.UP, 16, 0, 2, 0, sprite)
                .addFace(EnumFacing.DOWN, 0, 16, 14, 16, sprite)
                .addFace(EnumFacing.EAST, 14, 16, 0, 16, sprite)
                .addFace(EnumFacing.WEST, 0, 2, 0, 16, sprite)

                .addFace(EnumFacing.NORTH, hasEast ? 2 : 0, hasWest ? 14 : 16, hasUp ? 2 : 0, hasDown ? 14 : 16, sprite)
                .addFace(EnumFacing.SOUTH, hasEast ? 2 : 0, hasWest ? 14 : 16, hasUp ? 2 : 0, hasDown ? 14 : 16, sprite)

                .bake()
            );
        } else if (coverSide == EnumFacing.SOUTH) {
            handleAngle = 180;

            quads.addAll(QuadBuilder.withFormat(DefaultVertexFormats.ITEM)
                .setFrom(hasEast ? 14 : 16, hasDown ? 2 : 0, 16)
                .setTo(hasWest ? 2 : 0, hasUp ? 14 : 16, 14)

                .addFace(EnumFacing.UP, 0, 16, 14, 16, sprite)
                .addFace(EnumFacing.DOWN, 16, 0, 2, 0, sprite)
                .addFace(EnumFacing.EAST, 14, 16, 0, 16, sprite)
                .addFace(EnumFacing.WEST, 0, 2, 0, 16, sprite)

                .addFace(EnumFacing.NORTH, hasWest ? 2 : 0, hasEast ? 14 : 16, hasUp ? 2 : 0, hasDown ? 14 : 16, sprite)
                .addFace(EnumFacing.SOUTH, hasWest ? 2 : 0, hasEast ? 14 : 16, hasUp ? 2 : 0, hasDown ? 14 : 16, sprite)

                .bake()
            );
        } else if (coverSide == EnumFacing.EAST) {
            handleAngle = 270;

            quads.addAll(QuadBuilder.withFormat(DefaultVertexFormats.ITEM)
                .setFrom(14, hasDown ? 2 : 0, 0)
                .setTo(16, hasUp ? 14 : 16, 16)

                .addFace(EnumFacing.UP, 16, 14, 16, 0, sprite)
                .addFace(EnumFacing.DOWN, 14, 16, 0, 16, sprite)
                .addFace(EnumFacing.NORTH, 14, 16, 0, 16, sprite)
                .addFace(EnumFacing.SOUTH, 0, 2, 0, 16, sprite)

                .addFace(EnumFacing.EAST, 0, 16, hasUp ? 2 : 0, hasDown ? 14 : 16, sprite)
                .addFace(EnumFacing.WEST, 0, 16, hasUp ? 2 : 0, hasDown ? 14 : 16, sprite)

                .bake()
            );
        } else if (coverSide == EnumFacing.WEST) {
            handleAngle = 90;

            quads.addAll(QuadBuilder.withFormat(DefaultVertexFormats.ITEM)
                .setFrom(0, hasDown ? 2 : 0, 0)
                .setTo(2, hasUp ? 14 : 16, 16)

                .addFace(EnumFacing.UP, 2, 0, 16, 0, sprite)
                .addFace(EnumFacing.DOWN, 0, 2, 0, 16, sprite)
                .addFace(EnumFacing.NORTH, 0, 2, 0, 16, sprite)
                .addFace(EnumFacing.SOUTH, 14, 16, 0, 16, sprite)

                .addFace(EnumFacing.EAST, 0, 16, hasUp ? 2 : 0, hasDown ? 14 : 16, sprite)
                .addFace(EnumFacing.WEST, 0, 16, hasUp ? 2 : 0, hasDown ? 14 : 16, sprite)

                .bake()
            );
        } else if (coverSide == EnumFacing.DOWN) {
            handleAxis = EnumFacing.Axis.Z;
            handleAngle = 90;

            quads.addAll(QuadBuilder.withFormat(DefaultVertexFormats.ITEM)
                .setFrom(0, 0, 0)
                .setTo(16, 2, 16)

                .addFace(EnumFacing.NORTH, 0, 16, 14, 16, sprite)
                .addFace(EnumFacing.SOUTH, 0, 16, 14, 16, sprite)
                .addFace(EnumFacing.EAST, 0, 16, 14, 16, sprite)
                .addFace(EnumFacing.WEST, 0, 16, 14, 16, sprite)

                .addFace(EnumFacing.UP, 16, 0, 16, 0, sprite)
                .addFace(EnumFacing.DOWN, 0, 16, 0, 16, sprite)

                .bake()
            );
        } else if (coverSide == EnumFacing.UP) {
            handleAxis = EnumFacing.Axis.Z;
            handleAngle = 270;

            quads.addAll(QuadBuilder.withFormat(DefaultVertexFormats.ITEM)
                .setFrom(0, 14, 0)
                .setTo(16, 16, 16)

                .addFace(EnumFacing.NORTH, 0, 16, 0, 2, sprite)
                .addFace(EnumFacing.SOUTH, 0, 16, 0, 2, sprite)
                .addFace(EnumFacing.EAST, 0, 16, 0, 2, sprite)
                .addFace(EnumFacing.WEST, 0, 16, 0, 2, sprite)

                .addFace(EnumFacing.UP, 16, 0, 16, 0, sprite)
                .addFace(EnumFacing.DOWN, 0, 16, 0, 16, sprite)

                .bake()
            );
        }

        if (this.genericGreyTexture == null) {
            this.genericGreyTexture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(RS.ID + ":blocks/generic_grey");
        }

        quads.addAll(QuadBuilder.withFormat(DefaultVertexFormats.ITEM)
            .setFrom(7, 7, 2)
            .setTo(9, 9, 6)
            .addFace(EnumFacing.NORTH, 0, 0, 4, 4, genericGreyTexture)
            .addFace(EnumFacing.EAST, 0, 0, 2, 4, genericGreyTexture)
            .addFace(EnumFacing.SOUTH, 0, 0, 4, 4, genericGreyTexture)
            .addFace(EnumFacing.WEST, 0, 0, 2, 4, genericGreyTexture)
            .addFace(EnumFacing.UP, 0, 0, 4, 2, genericGreyTexture)
            .addFace(EnumFacing.DOWN, 0, 0, 4, 2, genericGreyTexture)
            .rotate(handleAxis, handleAngle)
            .bake()
        );
    }

    public static TextureAtlasSprite getSprite(IBakedModel coverModel, IBlockState coverState, EnumFacing facing, long rand) {
        TextureAtlasSprite sprite = null;

        BlockRenderLayer originalLayer = MinecraftForgeClient.getRenderLayer();

        try {
            for (BlockRenderLayer layer : BlockRenderLayer.values()) {
                ForgeHooksClient.setRenderLayer(layer);

                for (BakedQuad bakedQuad : coverModel.getQuads(coverState, facing, rand)) {
                    return bakedQuad.getSprite();
                }

                for (BakedQuad bakedQuad : coverModel.getQuads(coverState, null, rand)) {
                    if (sprite == null) {
                        sprite = bakedQuad.getSprite();
                    }

                    if (bakedQuad.getFace() == facing) {
                        return bakedQuad.getSprite();
                    }
                }
            }
        } catch (Exception e) {
            // NO OP
        } finally {
            ForgeHooksClient.setRenderLayer(originalLayer);
        }

        if (sprite == null) {
            try {
                sprite = coverModel.getParticleTexture();
            } catch (Exception e) {
                // NO OP
            }
        }

        if (sprite == null) {
            sprite = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
        }

        return sprite;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemCameraTransforms getItemCameraTransforms() {
        return base.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return base.getOverrides();
    }

    @Override
    public boolean isAmbientOcclusion(IBlockState state) {
        return base.isAmbientOcclusion(state);
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        return base.handlePerspective(cameraTransformType);
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
