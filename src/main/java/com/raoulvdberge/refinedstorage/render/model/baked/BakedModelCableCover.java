package com.raoulvdberge.refinedstorage.render.model.baked;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSBlocks;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.Cover;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.raoulvdberge.refinedstorage.block.BlockBase;
import com.raoulvdberge.refinedstorage.block.BlockCable;
import com.raoulvdberge.refinedstorage.render.CubeBuilder;
import com.raoulvdberge.refinedstorage.render.collision.constants.ConstantsCable;
import com.raoulvdberge.refinedstorage.util.RenderUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BakedModelCableCover extends BakedModelDelegate {
    private static TextureAtlasSprite GREY_SPRITE;

    public BakedModelCableCover(IBakedModel base) {
        super(base);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        List<BakedQuad> quads = new ArrayList<>(base.getQuads(state, side, rand));

        if (state != null) {
            IExtendedBlockState extendedState = (IExtendedBlockState) state;

            addCover(quads, extendedState.getValue(BlockCable.COVER_NORTH), EnumFacing.NORTH, side, rand, extendedState, true);
            addCover(quads, extendedState.getValue(BlockCable.COVER_SOUTH), EnumFacing.SOUTH, side, rand, extendedState, true);
            addCover(quads, extendedState.getValue(BlockCable.COVER_EAST), EnumFacing.EAST, side, rand, extendedState, true);
            addCover(quads, extendedState.getValue(BlockCable.COVER_WEST), EnumFacing.WEST, side, rand, extendedState, true);
            addCover(quads, extendedState.getValue(BlockCable.COVER_DOWN), EnumFacing.DOWN, side, rand, extendedState, true);
            addCover(quads, extendedState.getValue(BlockCable.COVER_UP), EnumFacing.UP, side, rand, extendedState, true);
        }

        return quads;
    }

    private static int getHollowCoverSize(@Nullable IBlockState state, EnumFacing coverSide) {
        if (state == null) {
            return 6;
        }

        BlockBase block = (BlockBase) state.getBlock();

        if (block.getDirection() != null && state.getValue(block.getDirection().getProperty()) == coverSide) {
            if (block == RSBlocks.CABLE || block == RSBlocks.EXPORTER) {
                return 6;
            } else if (block == RSBlocks.EXTERNAL_STORAGE || block == RSBlocks.IMPORTER) {
                return 3;
            } else if (block == RSBlocks.CONSTRUCTOR || block == RSBlocks.DESTRUCTOR || block == RSBlocks.READER || block == RSBlocks.WRITER) {
                return 2;
            }
        }

        return 6;
    }

    protected static void addCover(List<BakedQuad> quads, @Nullable Cover cover, EnumFacing coverSide, EnumFacing side, long rand, @Nullable IExtendedBlockState state, boolean handle) {
        if (cover == null) {
            return;
        }

        IBlockState coverState = CoverManager.getBlockState(cover.getStack());

        if (coverState == null) {
            return;
        }

        boolean hasUp = false, hasDown = false, hasEast = false, hasWest = false;

        if (state != null) {
            hasUp = state.getValue(BlockCable.COVER_UP) != null;
            hasDown = state.getValue(BlockCable.COVER_DOWN) != null;
            hasEast = state.getValue(BlockCable.COVER_EAST) != null;
            hasWest = state.getValue(BlockCable.COVER_WEST) != null;
        }

        TextureAtlasSprite sprite = RenderUtils.getSprite(Minecraft.getMinecraft().getBlockRendererDispatcher().getModelForState(coverState), coverState, side, rand);

        switch (cover.getType()) {
            case NORMAL:
                addNormalCover(quads, sprite, coverSide, hasUp, hasDown, hasEast, hasWest, handle);
                break;
            case HOLLOW:
                addHollowCover(quads, sprite, coverSide, hasUp, hasDown, hasEast, hasWest, getHollowCoverSize(state, coverSide));
                break;
        }
    }

    private static void addNormalCover(List<BakedQuad> quads, TextureAtlasSprite sprite, EnumFacing coverSide, boolean hasUp, boolean hasDown, boolean hasEast, boolean hasWest, boolean handle) {
        AxisAlignedBB bounds = ConstantsCable.getCoverBounds(coverSide);

        Vector3f from = new Vector3f((float) bounds.minX * 16, (float) bounds.minY * 16, (float) bounds.minZ * 16);
        Vector3f to = new Vector3f((float) bounds.maxX * 16, (float) bounds.maxY * 16, (float) bounds.maxZ * 16);

        if (coverSide == EnumFacing.NORTH) {
            if (hasWest) {
                from.setX(2);
            }

            if (hasEast) {
                to.setX(14);
            }
        } else if (coverSide == EnumFacing.SOUTH) {
            if (hasWest) {
                from.setX(2);
            }

            if (hasEast) {
                to.setX(14);
            }
        }

        if (coverSide.getAxis() != EnumFacing.Axis.Y) {
            if (hasDown) {
                from.setY(2);
            }

            if (hasUp) {
                to.setY(14);
            }
        }

        quads.addAll(new CubeBuilder().from(from.getX(), from.getY(), from.getZ()).to(to.getX(), to.getY(), to.getZ()).addFaces(face -> new CubeBuilder.Face(face, sprite)).bake());

        if (handle) {
            if (GREY_SPRITE == null) {
                GREY_SPRITE = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(RS.ID + ":blocks/generic_grey");
            }

            bounds = ConstantsCable.getHolderBounds(coverSide);

            from = new Vector3f((float) bounds.minX * 16, (float) bounds.minY * 16, (float) bounds.minZ * 16);
            to = new Vector3f((float) bounds.maxX * 16, (float) bounds.maxY * 16, (float) bounds.maxZ * 16);

            quads.addAll(new CubeBuilder().from(from.getX(), from.getY(), from.getZ()).to(to.getX(), to.getY(), to.getZ()).addFaces(face -> new CubeBuilder.Face(face, GREY_SPRITE)).bake());
        }
    }

    private static void addHollowCover(List<BakedQuad> quads, TextureAtlasSprite sprite, EnumFacing coverSide, boolean hasUp, boolean hasDown, boolean hasEast, boolean hasWest, int size) {
        AxisAlignedBB bounds = ConstantsCable.getCoverBounds(coverSide);

        Vector3f from = new Vector3f((float) bounds.minX * 16, (float) bounds.minY * 16, (float) bounds.minZ * 16);
        Vector3f to = new Vector3f((float) bounds.maxX * 16, (float) bounds.maxY * 16, (float) bounds.maxZ * 16);

        if (coverSide.getAxis() != EnumFacing.Axis.Y) {
            if (hasDown) {
                from.setY(2);
            }

            if (hasUp) {
                to.setY(14);
            }
        }

        // Right
        if (coverSide == EnumFacing.NORTH) {
            if (hasWest) {
                from.setX(2);
            } else {
                from.setX(0);
            }

            to.setX(size);
        } else if (coverSide == EnumFacing.SOUTH) {
            if (hasEast) {
                to.setX(14);
            } else {
                to.setX(16);
            }

            from.setX(16 - size);
        } else if (coverSide == EnumFacing.EAST) {
            from.setZ(0);
            to.setZ(size);
        } else if (coverSide == EnumFacing.WEST) {
            from.setZ(16 - size);
            to.setZ(16);
        } else if (coverSide == EnumFacing.DOWN || coverSide == EnumFacing.UP) {
            from.setZ(16 - size);
            to.setZ(16);
        }

        quads.addAll(new CubeBuilder()
            .from(from.getX(), from.getY(), from.getZ())
            .to(to.getX(), to.getY(), to.getZ())
            .addFaces(face -> new CubeBuilder.Face(face, sprite))
            .bake()
        );

        // Left
        if (coverSide == EnumFacing.NORTH) {
            if (hasEast) {
                to.setX(14);
            } else {
                to.setX(16);
            }

            from.setX(16 - size);
        } else if (coverSide == EnumFacing.SOUTH) {
            if (hasWest) {
                from.setX(2);
            } else {
                from.setX(0);
            }

            to.setX(size);
        } else if (coverSide == EnumFacing.EAST) {
            from.setZ(16 - size);
            to.setZ(16);
        } else if (coverSide == EnumFacing.WEST) {
            from.setZ(0);
            to.setZ(size);
        } else if (coverSide == EnumFacing.DOWN || coverSide == EnumFacing.UP) {
            from.setZ(0);
            to.setZ(size);
        }

        quads.addAll(new CubeBuilder()
            .from(from.getX(), from.getY(), from.getZ())
            .to(to.getX(), to.getY(), to.getZ())
            .addFaces(face -> new CubeBuilder.Face(face, sprite))
            .bake()
        );

        // Bottom
        if (coverSide == EnumFacing.NORTH) {
            from.setX(size);
            to.setX(16 - size);

            if (hasDown) {
                from.setY(2);
            } else {
                from.setY(0);
            }

            to.setY(size);
        } else if (coverSide == EnumFacing.SOUTH) {
            from.setX(size);
            to.setX(16 - size);

            if (hasDown) {
                from.setY(2);
            } else {
                from.setY(0);
            }

            to.setY(size);
        } else if (coverSide == EnumFacing.EAST) {
            from.setZ(size);
            to.setZ(16 - size);

            if (hasDown) {
                from.setY(2);
            } else {
                from.setY(0);
            }

            to.setY(size);
        } else if (coverSide == EnumFacing.WEST) {
            from.setZ(size);
            to.setZ(16 - size);

            if (hasDown) {
                from.setY(2);
            } else {
                from.setY(0);
            }

            to.setY(size);
        } else if (coverSide == EnumFacing.DOWN || coverSide == EnumFacing.UP) {
            from.setZ(size);
            to.setZ(16 - size);

            from.setX(0);
            to.setX(size);
        }

        quads.addAll(new CubeBuilder()
            .from(from.getX(), from.getY(), from.getZ())
            .to(to.getX(), to.getY(), to.getZ())
            .addFaces(face -> new CubeBuilder.Face(face, sprite))
            .bake()
        );

        // Up
        if (coverSide == EnumFacing.NORTH) {
            from.setX(size);
            to.setX(16 - size);

            if (hasUp) {
                to.setY(14);
            } else {
                to.setY(16);
            }

            from.setY(16 - size);
        } else if (coverSide == EnumFacing.SOUTH) {
            from.setX(size);
            to.setX(16 - size);

            if (hasUp) {
                to.setY(14);
            } else {
                to.setY(16);
            }

            from.setY(16 - size);
        } else if (coverSide == EnumFacing.EAST) {
            from.setZ(size);
            to.setZ(16 - size);

            if (hasUp) {
                to.setY(14);
            } else {
                to.setY(16);
            }

            from.setY(16 - size);
        } else if (coverSide == EnumFacing.WEST) {
            from.setZ(size);
            to.setZ(16 - size);

            if (hasUp) {
                to.setY(14);
            } else {
                to.setY(16);
            }

            from.setY(16 - size);
        } else if (coverSide == EnumFacing.DOWN || coverSide == EnumFacing.UP) {
            from.setZ(size);
            to.setZ(16 - size);

            from.setX(16 - size);
            to.setX(16);
        }

        quads.addAll(new CubeBuilder()
            .from(from.getX(), from.getY(), from.getZ())
            .to(to.getX(), to.getY(), to.getZ())
            .addFaces(face -> new CubeBuilder.Face(face, sprite))
            .bake()
        );
    }
}
