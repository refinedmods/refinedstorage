package com.raoulvdberge.refinedstorage.render.model.baked;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.raoulvdberge.refinedstorage.block.BlockCable;
import com.raoulvdberge.refinedstorage.render.CubeBuilder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
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
    private static TextureAtlasSprite GREY_SPRITE;

    private IBakedModel base;

    public BakedModelCableCover(IBakedModel base) {
        this.base = base;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        List<BakedQuad> quads = new ArrayList<>(base.getQuads(state, side, rand));

        if (state != null) {
            IExtendedBlockState s = (IExtendedBlockState) state;

            boolean hasUp = CoverManager.getBlockState(s.getValue(BlockCable.COVER_UP)) != null;
            boolean hasDown = CoverManager.getBlockState(s.getValue(BlockCable.COVER_DOWN)) != null;

            boolean hasEast = CoverManager.getBlockState(s.getValue(BlockCable.COVER_EAST)) != null;
            boolean hasWest = CoverManager.getBlockState(s.getValue(BlockCable.COVER_WEST)) != null;

            addCover(quads, s.getValue(BlockCable.COVER_NORTH), EnumFacing.NORTH, side, rand, hasUp, hasDown, hasEast, hasWest);
            addCover(quads, s.getValue(BlockCable.COVER_SOUTH), EnumFacing.SOUTH, side, rand, hasUp, hasDown, hasEast, hasWest);
            addCover(quads, s.getValue(BlockCable.COVER_EAST), EnumFacing.EAST, side, rand, hasUp, hasDown, hasEast, hasWest);
            addCover(quads, s.getValue(BlockCable.COVER_WEST), EnumFacing.WEST, side, rand, hasUp, hasDown, hasEast, hasWest);
            addCover(quads, s.getValue(BlockCable.COVER_DOWN), EnumFacing.DOWN, side, rand, hasUp, hasDown, hasEast, hasWest);
            addCover(quads, s.getValue(BlockCable.COVER_UP), EnumFacing.UP, side, rand, hasUp, hasDown, hasEast, hasWest);
        }

        return quads;
    }

    protected static void addCover(List<BakedQuad> quads, @Nullable ItemStack coverStack, EnumFacing coverSide, EnumFacing side, long rand, boolean hasUp, boolean hasDown, boolean hasEast, boolean hasWest) {
        if (coverStack == null || coverStack.isEmpty()) {
            return;
        }

        IBlockState coverState = CoverManager.getBlockState(coverStack);

        if (coverState == null) {
            return;
        }

        IBakedModel coverModel = Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(coverState);

        TextureAtlasSprite sprite = getSprite(coverModel, coverState, side, rand);

        ModelRotation modelRotation = ModelRotation.X0_Y0;

        int xStart = 0;
        int xEnd = 16;
        int xTexStart = 0;
        int xTexEnd = 16;
        int xTexBackStart = 0;
        int xTexBackEnd = 16;

        int yStart = 0;
        int yEnd = 16;
        int yTexStart = 0;
        int yTexEnd = 16;

        if (coverSide == EnumFacing.NORTH) {
            if (hasWest) {
                xStart = 2;
                xTexEnd = 14;
                xTexBackStart = 2;
            }

            if (hasEast) {
                xEnd = 14;
                xTexStart = 2;
                xTexBackEnd = 14;
            }
        } else if (coverSide == EnumFacing.SOUTH) {
            modelRotation = ModelRotation.X0_Y180;

            if (hasWest) {
                xEnd = 14;
                xTexStart = 2;
                xTexBackEnd = 14;
            }

            if (hasEast) {
                xStart = 2;
                xTexEnd = 14;
                xTexBackStart = 2;
            }
        } else if (coverSide == EnumFacing.EAST) {
            modelRotation = ModelRotation.X0_Y90;
        } else if (coverSide == EnumFacing.WEST) {
            modelRotation = ModelRotation.X0_Y270;
        } else if (coverSide == EnumFacing.DOWN) {
            modelRotation = ModelRotation.X90_Y0;
        } else if (coverSide == EnumFacing.UP) {
            modelRotation = ModelRotation.X270_Y0;
        }

        if (coverSide.getAxis() == EnumFacing.Axis.X || coverSide.getAxis() == EnumFacing.Axis.Z) {
            if (hasDown) {
                yStart = 2;
                yTexEnd = 14;
            }

            if (hasUp) {
                yEnd = 14;
                yTexStart = 2;
            }
        }

        quads.addAll(new CubeBuilder()
            .from(xStart, yStart, 0)
            .to(xEnd, yEnd, 2)

            .face(EnumFacing.NORTH, xTexStart, xTexEnd, yTexStart, yTexEnd, sprite)
            .face(EnumFacing.SOUTH, xTexBackStart, xTexBackEnd, yTexStart, yTexEnd, sprite)

            .face(EnumFacing.UP, 0, 16, 0, 2, sprite)
            .face(EnumFacing.DOWN, 0, 16, 14, 16, sprite)
            .face(EnumFacing.EAST, 14, 16, 0, 16, sprite)
            .face(EnumFacing.WEST, 0, 2, 0, 16, sprite)

            .rotate(modelRotation)

            .bake()
        );

        if (GREY_SPRITE == null) {
            GREY_SPRITE = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(RS.ID + ":blocks/generic_grey");
        }

        quads.addAll(new CubeBuilder()
            .from(7, 7, 2)
            .to(9, 9, 6)

            .face(EnumFacing.NORTH, 0, 0, 4, 4, GREY_SPRITE)
            .face(EnumFacing.EAST, 0, 0, 2, 4, GREY_SPRITE)
            .face(EnumFacing.SOUTH, 0, 0, 4, 4, GREY_SPRITE)
            .face(EnumFacing.WEST, 0, 0, 2, 4, GREY_SPRITE)
            .face(EnumFacing.UP, 0, 0, 4, 2, GREY_SPRITE)
            .face(EnumFacing.DOWN, 0, 0, 4, 2, GREY_SPRITE)

            .rotate(modelRotation)

            .setUvLocked(false)

            .bake()
        );
    }

    private static TextureAtlasSprite getSprite(IBakedModel coverModel, IBlockState coverState, EnumFacing facing, long rand) {
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
