package com.raoulvdberge.refinedstorage.render.model.baked;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.Cover;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.raoulvdberge.refinedstorage.block.BlockCable;
import com.raoulvdberge.refinedstorage.render.CubeBuilder;
import com.raoulvdberge.refinedstorage.render.constants.ConstantsCable;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.util.vector.Vector3f;

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

            boolean hasUp = s.getValue(BlockCable.COVER_UP) != null;
            boolean hasDown = s.getValue(BlockCable.COVER_DOWN) != null;

            boolean hasEast = s.getValue(BlockCable.COVER_EAST) != null;
            boolean hasWest = s.getValue(BlockCable.COVER_WEST) != null;

            addCover(quads, s.getValue(BlockCable.COVER_NORTH), EnumFacing.NORTH, side, rand, hasUp, hasDown, hasEast, hasWest, true);
            addCover(quads, s.getValue(BlockCable.COVER_SOUTH), EnumFacing.SOUTH, side, rand, hasUp, hasDown, hasEast, hasWest, true);
            addCover(quads, s.getValue(BlockCable.COVER_EAST), EnumFacing.EAST, side, rand, hasUp, hasDown, hasEast, hasWest, true);
            addCover(quads, s.getValue(BlockCable.COVER_WEST), EnumFacing.WEST, side, rand, hasUp, hasDown, hasEast, hasWest, true);
            addCover(quads, s.getValue(BlockCable.COVER_DOWN), EnumFacing.DOWN, side, rand, hasUp, hasDown, hasEast, hasWest, true);
            addCover(quads, s.getValue(BlockCable.COVER_UP), EnumFacing.UP, side, rand, hasUp, hasDown, hasEast, hasWest, true);
        }

        return quads;
    }

    protected static void addCover(List<BakedQuad> quads, @Nullable Cover cover, EnumFacing coverSide, EnumFacing side, long rand, boolean hasUp, boolean hasDown, boolean hasEast, boolean hasWest, boolean handle) {
        if (cover == null) {
            return;
        }

        IBlockState coverState = CoverManager.getBlockState(cover.getStack());

        if (coverState == null) {
            return;
        }

        TextureAtlasSprite sprite = RenderUtils.getSprite(Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(coverState), coverState, side, rand);

        switch (cover.getType()) {
            case NORMAL:
                addNormalCover(quads, sprite, coverSide, hasUp, hasDown, hasEast, hasWest, handle);
                break;
            case HOLLOW:
                addHollowCover(quads, sprite, coverSide, hasUp, hasDown, hasEast, hasWest);
                break;
            case HOLLOW_WIDE:
                addHollowWideCover(quads, sprite, coverSide, hasUp, hasDown, hasEast, hasWest);
                break;
        }
    }

    private static void addNormalCover(List<BakedQuad> quads, TextureAtlasSprite sprite, EnumFacing coverSide, boolean hasUp, boolean hasDown, boolean hasEast, boolean hasWest, boolean handle) {
        Pair<Vector3f, Vector3f> bounds = ConstantsCable.getCoverBounds(coverSide);

        if (coverSide == EnumFacing.NORTH) {
            if (hasWest) {
                bounds.getLeft().setX(2);
            }

            if (hasEast) {
                bounds.getRight().setX(14);
            }
        } else if (coverSide == EnumFacing.SOUTH) {
            if (hasWest) {
                bounds.getLeft().setX(2);
            }

            if (hasEast) {
                bounds.getRight().setX(14);
            }
        }

        if (coverSide.getAxis() != EnumFacing.Axis.Y) {
            if (hasDown) {
                bounds.getLeft().setY(2);
            }

            if (hasUp) {
                bounds.getRight().setY(14);
            }
        }

        quads.addAll(new CubeBuilder()
            .from(bounds.getLeft().getX(), bounds.getLeft().getY(), bounds.getLeft().getZ())
            .to(bounds.getRight().getX(), bounds.getRight().getY(), bounds.getRight().getZ())
            .addFaces(face -> new CubeBuilder.Face(face, sprite))
            .bake()
        );

        if (handle) {
            if (GREY_SPRITE == null) {
                GREY_SPRITE = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(RS.ID + ":blocks/generic_grey");
            }

            bounds = ConstantsCable.getHolderBounds(coverSide);

            quads.addAll(new CubeBuilder()
                .from(bounds.getLeft().getX(), bounds.getLeft().getY(), bounds.getLeft().getZ())
                .to(bounds.getRight().getX(), bounds.getRight().getY(), bounds.getRight().getZ())

                .addFaces(face -> new CubeBuilder.Face(face, GREY_SPRITE))

                .bake()
            );
        }
    }

    private static void addHollowCover(List<BakedQuad> quads, TextureAtlasSprite sprite, EnumFacing coverSide, boolean hasUp, boolean hasDown, boolean hasEast, boolean hasWest) {
        Pair<Vector3f, Vector3f> bounds = ConstantsCable.getCoverBounds(coverSide);

        if (coverSide.getAxis() != EnumFacing.Axis.Y) {
            if (hasDown) {
                bounds.getLeft().setY(2);
            }

            if (hasUp) {
                bounds.getRight().setY(14);
            }
        }

        // Right
        if (coverSide == EnumFacing.NORTH) {
            if (hasWest) {
                bounds.getLeft().setX(2);
            } else {
                bounds.getLeft().setX(0);
            }

            bounds.getRight().setX(6);
        } else if (coverSide == EnumFacing.SOUTH) {
            if (hasEast) {
                bounds.getRight().setX(14);
            } else {
                bounds.getRight().setX(16);
            }

            bounds.getLeft().setX(10);
        } else if (coverSide == EnumFacing.EAST) {
            bounds.getLeft().setZ(0);
            bounds.getRight().setZ(6);
        } else if (coverSide == EnumFacing.WEST) {
            bounds.getLeft().setZ(10);
            bounds.getRight().setZ(16);
        } else if (coverSide == EnumFacing.DOWN || coverSide == EnumFacing.UP) {
            bounds.getLeft().setZ(10);
            bounds.getRight().setZ(16);
        }

        quads.addAll(new CubeBuilder()
            .from(bounds.getLeft().getX(), bounds.getLeft().getY(), bounds.getLeft().getZ())
            .to(bounds.getRight().getX(), bounds.getRight().getY(), bounds.getRight().getZ())
            .addFaces(face -> new CubeBuilder.Face(face, sprite))
            .bake()
        );

        // Left
        if (coverSide == EnumFacing.NORTH) {
            if (hasEast) {
                bounds.getRight().setX(14);
            } else {
                bounds.getRight().setX(16);
            }

            bounds.getLeft().setX(10);
        } else if (coverSide == EnumFacing.SOUTH) {
            if (hasWest) {
                bounds.getLeft().setX(2);
            } else {
                bounds.getLeft().setX(0);
            }

            bounds.getRight().setX(6);
        } else if (coverSide == EnumFacing.EAST) {
            bounds.getRight().setZ(16);
            bounds.getLeft().setZ(10);
        } else if (coverSide == EnumFacing.WEST) {
            bounds.getLeft().setZ(0);
            bounds.getRight().setZ(6);
        } else if (coverSide == EnumFacing.DOWN || coverSide == EnumFacing.UP) {
            bounds.getLeft().setZ(0);
            bounds.getRight().setZ(6);
        }

        quads.addAll(new CubeBuilder()
            .from(bounds.getLeft().getX(), bounds.getLeft().getY(), bounds.getLeft().getZ())
            .to(bounds.getRight().getX(), bounds.getRight().getY(), bounds.getRight().getZ())
            .addFaces(face -> new CubeBuilder.Face(face, sprite))
            .bake()
        );

        // Bottom
        if (coverSide == EnumFacing.NORTH) {
            bounds.getLeft().setX(6);
            bounds.getRight().setX(10);

            if (hasDown) {
                bounds.getLeft().setY(2);
            } else {
                bounds.getLeft().setY(0);
            }

            bounds.getRight().setY(6);
        } else if (coverSide == EnumFacing.SOUTH) {
            bounds.getLeft().setX(6);
            bounds.getRight().setX(10);

            if (hasDown) {
                bounds.getLeft().setY(2);
            } else {
                bounds.getLeft().setY(0);
            }

            bounds.getRight().setY(6);
        } else if (coverSide == EnumFacing.EAST) {
            bounds.getLeft().setZ(6);
            bounds.getRight().setZ(10);

            if (hasDown) {
                bounds.getLeft().setY(2);
            } else {
                bounds.getLeft().setY(0);
            }

            bounds.getRight().setY(6);
        } else if (coverSide == EnumFacing.WEST) {
            bounds.getLeft().setZ(6);
            bounds.getRight().setZ(10);

            if (hasDown) {
                bounds.getLeft().setY(2);
            } else {
                bounds.getLeft().setY(0);
            }

            bounds.getRight().setY(6);
        } else if (coverSide == EnumFacing.DOWN || coverSide == EnumFacing.UP) {
            bounds.getLeft().setZ(6);
            bounds.getRight().setZ(10);

            bounds.getLeft().setX(0);
            bounds.getRight().setX(6);
        }

        quads.addAll(new CubeBuilder()
            .from(bounds.getLeft().getX(), bounds.getLeft().getY(), bounds.getLeft().getZ())
            .to(bounds.getRight().getX(), bounds.getRight().getY(), bounds.getRight().getZ())
            .addFaces(face -> new CubeBuilder.Face(face, sprite))
            .bake()
        );

        // Up
        if (coverSide == EnumFacing.NORTH) {
            bounds.getLeft().setX(6);
            bounds.getRight().setX(10);

            if (hasUp) {
                bounds.getRight().setY(14);
            } else {
                bounds.getRight().setY(16);
            }

            bounds.getLeft().setY(10);
        } else if (coverSide == EnumFacing.SOUTH) {
            bounds.getLeft().setX(6);
            bounds.getRight().setX(10);

            if (hasUp) {
                bounds.getRight().setY(14);
            } else {
                bounds.getRight().setY(16);
            }

            bounds.getLeft().setY(10);
        } else if (coverSide == EnumFacing.EAST) {
            bounds.getLeft().setZ(6);
            bounds.getRight().setZ(10);

            if (hasUp) {
                bounds.getRight().setY(14);
            } else {
                bounds.getRight().setY(16);
            }

            bounds.getLeft().setY(10);
        } else if (coverSide == EnumFacing.WEST) {
            bounds.getLeft().setZ(6);
            bounds.getRight().setZ(10);

            if (hasUp) {
                bounds.getRight().setY(14);
            } else {
                bounds.getRight().setY(16);
            }

            bounds.getLeft().setY(10);
        } else if (coverSide == EnumFacing.DOWN || coverSide == EnumFacing.UP) {
            bounds.getLeft().setZ(6);
            bounds.getRight().setZ(10);

            bounds.getLeft().setX(10);
            bounds.getRight().setX(16);
        }

        quads.addAll(new CubeBuilder()
            .from(bounds.getLeft().getX(), bounds.getLeft().getY(), bounds.getLeft().getZ())
            .to(bounds.getRight().getX(), bounds.getRight().getY(), bounds.getRight().getZ())
            .addFaces(face -> new CubeBuilder.Face(face, sprite))
            .bake()
        );
    }

    private static void addHollowWideCover(List<BakedQuad> quads, TextureAtlasSprite sprite, EnumFacing coverSide, boolean hasUp, boolean hasDown, boolean hasEast, boolean hasWest) {
        Pair<Vector3f, Vector3f> bounds = ConstantsCable.getCoverBounds(coverSide);

        if (coverSide.getAxis() != EnumFacing.Axis.Y) {
            if (hasDown) {
                bounds.getLeft().setY(2);
            }

            if (hasUp) {
                bounds.getRight().setY(14);
            }
        }

        // Right
        if (coverSide == EnumFacing.NORTH) {
            if (hasWest) {
                bounds.getLeft().setX(2);
            } else {
                bounds.getLeft().setX(0);
            }

            bounds.getRight().setX(3);
        } else if (coverSide == EnumFacing.SOUTH) {
            if (hasEast) {
                bounds.getRight().setX(14);
            } else {
                bounds.getRight().setX(16);
            }

            bounds.getLeft().setX(13);
        } else if (coverSide == EnumFacing.EAST) {
            bounds.getLeft().setZ(0);
            bounds.getRight().setZ(3);
        } else if (coverSide == EnumFacing.WEST) {
            bounds.getLeft().setZ(13);
            bounds.getRight().setZ(16);
        } else if (coverSide == EnumFacing.DOWN || coverSide == EnumFacing.UP) {
            bounds.getLeft().setZ(13);
            bounds.getRight().setZ(16);
        }

        quads.addAll(new CubeBuilder()
            .from(bounds.getLeft().getX(), bounds.getLeft().getY(), bounds.getLeft().getZ())
            .to(bounds.getRight().getX(), bounds.getRight().getY(), bounds.getRight().getZ())
            .addFaces(face -> new CubeBuilder.Face(face, sprite))
            .bake()
        );

        // Left
        if (coverSide == EnumFacing.NORTH) {
            if (hasEast) {
                bounds.getRight().setX(14);
            } else {
                bounds.getRight().setX(16);
            }

            bounds.getLeft().setX(13);
        } else if (coverSide == EnumFacing.SOUTH) {
            if (hasWest) {
                bounds.getLeft().setX(2);
            } else {
                bounds.getLeft().setX(0);
            }

            bounds.getRight().setX(3);
        } else if (coverSide == EnumFacing.EAST) {
            bounds.getRight().setZ(16);
            bounds.getLeft().setZ(13);
        } else if (coverSide == EnumFacing.WEST) {
            bounds.getLeft().setZ(0);
            bounds.getRight().setZ(3);
        } else if (coverSide == EnumFacing.DOWN || coverSide == EnumFacing.UP) {
            bounds.getLeft().setZ(0);
            bounds.getRight().setZ(3);
        }

        quads.addAll(new CubeBuilder()
            .from(bounds.getLeft().getX(), bounds.getLeft().getY(), bounds.getLeft().getZ())
            .to(bounds.getRight().getX(), bounds.getRight().getY(), bounds.getRight().getZ())
            .addFaces(face -> new CubeBuilder.Face(face, sprite))
            .bake()
        );

        // Bottom
        if (coverSide == EnumFacing.NORTH) {
            bounds.getLeft().setX(3);
            bounds.getRight().setX(13);

            if (hasDown) {
                bounds.getLeft().setY(2);
            } else {
                bounds.getLeft().setY(0);
            }

            bounds.getRight().setY(3);
        } else if (coverSide == EnumFacing.SOUTH) {
            bounds.getLeft().setX(3);
            bounds.getRight().setX(13);

            if (hasDown) {
                bounds.getLeft().setY(2);
            } else {
                bounds.getLeft().setY(0);
            }

            bounds.getRight().setY(3);
        } else if (coverSide == EnumFacing.EAST) {
            bounds.getLeft().setZ(3);
            bounds.getRight().setZ(13);

            if (hasDown) {
                bounds.getLeft().setY(2);
            } else {
                bounds.getLeft().setY(0);
            }

            bounds.getRight().setY(3);
        } else if (coverSide == EnumFacing.WEST) {
            bounds.getLeft().setZ(3);
            bounds.getRight().setZ(13);

            if (hasDown) {
                bounds.getLeft().setY(2);
            } else {
                bounds.getLeft().setY(0);
            }

            bounds.getRight().setY(3);
        } else if (coverSide == EnumFacing.DOWN || coverSide == EnumFacing.UP) {
            bounds.getLeft().setZ(3);
            bounds.getRight().setZ(13);

            bounds.getLeft().setX(0);
            bounds.getRight().setX(3);
        }

        quads.addAll(new CubeBuilder()
            .from(bounds.getLeft().getX(), bounds.getLeft().getY(), bounds.getLeft().getZ())
            .to(bounds.getRight().getX(), bounds.getRight().getY(), bounds.getRight().getZ())
            .addFaces(face -> new CubeBuilder.Face(face, sprite))
            .bake()
        );

        // Up
        if (coverSide == EnumFacing.NORTH) {
            bounds.getLeft().setX(3);
            bounds.getRight().setX(13);

            if (hasUp) {
                bounds.getRight().setY(14);
            } else {
                bounds.getRight().setY(16);
            }

            bounds.getLeft().setY(13);
        } else if (coverSide == EnumFacing.SOUTH) {
            bounds.getLeft().setX(3);
            bounds.getRight().setX(13);

            if (hasUp) {
                bounds.getRight().setY(14);
            } else {
                bounds.getRight().setY(16);
            }

            bounds.getLeft().setY(13);
        } else if (coverSide == EnumFacing.EAST) {
            bounds.getLeft().setZ(3);
            bounds.getRight().setZ(13);

            if (hasUp) {
                bounds.getRight().setY(14);
            } else {
                bounds.getRight().setY(16);
            }

            bounds.getLeft().setY(13);
        } else if (coverSide == EnumFacing.WEST) {
            bounds.getLeft().setZ(3);
            bounds.getRight().setZ(13);

            if (hasUp) {
                bounds.getRight().setY(14);
            } else {
                bounds.getRight().setY(16);
            }

            bounds.getLeft().setY(13);
        } else if (coverSide == EnumFacing.DOWN || coverSide == EnumFacing.UP) {
            bounds.getLeft().setZ(3);
            bounds.getRight().setZ(13);

            bounds.getLeft().setX(13);
            bounds.getRight().setX(16);
        }

        quads.addAll(new CubeBuilder()
            .from(bounds.getLeft().getX(), bounds.getLeft().getY(), bounds.getLeft().getZ())
            .to(bounds.getRight().getX(), bounds.getRight().getY(), bounds.getRight().getZ())
            .addFaces(face -> new CubeBuilder.Face(face, sprite))
            .bake()
        );
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
