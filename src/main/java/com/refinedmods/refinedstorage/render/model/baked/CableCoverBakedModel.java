package com.refinedmods.refinedstorage.render.model.baked;

import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.RSBlocks;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.Cover;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.CoverManager;
import com.refinedmods.refinedstorage.block.BaseBlock;
import com.refinedmods.refinedstorage.render.ConstantsCable;
import com.refinedmods.refinedstorage.render.model.CubeBuilder;
import com.refinedmods.refinedstorage.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.model.BakedModelWrapper;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CableCoverBakedModel extends BakedModelWrapper<BakedModel> {

    private static TextureAtlasSprite BORDER_SPRITE;

    public CableCoverBakedModel(BakedModel base) {
        super(base);
    }

    private static int getHollowCoverSize(@Nullable BlockState state, Direction coverSide) {
        if (state == null) {
            return 6;
        }

        BaseBlock block = (BaseBlock) state.getBlock();
        if (block == RSBlocks.CABLE.get()) {
            return 6;
        }

        if (block.getDirection() != null && state.getValue(block.getDirection().getProperty()) == coverSide) {
            if (block == RSBlocks.EXPORTER.get()) {
                return 6;
            } else if (block == RSBlocks.EXTERNAL_STORAGE.get() || block == RSBlocks.IMPORTER.get()) {
                return 3;
            } else if (block == RSBlocks.CONSTRUCTOR.get() || block == RSBlocks.DESTRUCTOR.get()) { //Removed reader and writer
                return 2;
            }
        }

        return 6;
    }

    protected static void addCover(List<BakedQuad> quads, @Nullable Cover cover, Direction coverSide, Direction side, RandomSource rand, @Nullable CoverManager manager, BlockState state, boolean handle) {
        if (cover == null) {
            return;
        }

        BlockState coverState = CoverManager.getBlockState(cover.getStack());

        if (coverState == null) {
            return;
        }

        boolean hasUp = false, hasDown = false, hasEast = false, hasWest = false;

        if (manager != null) {
            hasUp = manager.hasCover(Direction.UP);
            hasDown = manager.hasCover(Direction.DOWN);
            hasEast = manager.hasCover(Direction.EAST);
            hasWest = manager.hasCover(Direction.WEST);
        }

        switch (cover.getType()) {
            case NORMAL -> addNormalCover(quads, coverState, coverSide, hasUp, hasDown, hasEast, hasWest, handle, rand);
            case HOLLOW ->
                addHollowCover(quads, coverState, coverSide, hasUp, hasDown, hasEast, hasWest, getHollowCoverSize(state, coverSide), rand);
        }
    }

    private static void addNormalCover(List<BakedQuad> quads, BlockState state, Direction coverSide, boolean hasUp, boolean hasDown, boolean hasEast, boolean hasWest, boolean handle, RandomSource random) {
        AABB bounds = ConstantsCable.getCoverBounds(coverSide);

        float fromX = (float) bounds.minX * 16;
        float fromY = (float) bounds.minY * 16;
        float fromZ = (float) bounds.minZ * 16;

        float toX = (float) bounds.maxX * 16;
        float toY = (float) bounds.maxY * 16;
        float toZ = (float) bounds.maxZ * 16;

        if (coverSide == Direction.NORTH) {
            if (hasWest) {
                fromX = 2;
            }

            if (hasEast) {
                toX = 14;
            }
        } else if (coverSide == Direction.SOUTH) {
            if (hasWest) {
                fromX = 2;
            }

            if (hasEast) {
                toX = 14;
            }
        }

        if (coverSide.getAxis() != Direction.Axis.Y) {
            if (hasDown) {
                fromY = 2;
            }

            if (hasUp) {
                toY = 14;
            }
        }

        Vector3f from = new Vector3f(fromX, fromY, fromZ);
        Vector3f to = new Vector3f(toX, toY, toZ);

        HashMap<Direction, TextureAtlasSprite> spriteCache = new HashMap<>();  //Changed from 1.12: to improve sprite getting for each side
        quads.addAll(new CubeBuilder().from(from.x(), from.y(), from.z()).to(to.x(), to.y(), to.z()).addFaces(face -> new CubeBuilder.Face(face, spriteCache.computeIfAbsent(face, direction -> RenderUtils.getSprite(Minecraft.getInstance().getBlockRenderer().getBlockModel(state), state, direction, random)))).bake());

        if (handle) {
            if (BORDER_SPRITE == null) {
                BORDER_SPRITE = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(new ResourceLocation(RS.ID, "block/cable_part_border"));
            }

            bounds = ConstantsCable.getHolderBounds(coverSide);

            from = new Vector3f((float) bounds.minX * 16, (float) bounds.minY * 16, (float) bounds.minZ * 16);
            to = new Vector3f((float) bounds.maxX * 16, (float) bounds.maxY * 16, (float) bounds.maxZ * 16);

            quads.addAll(new CubeBuilder().from(from.x(), from.y(), from.z()).to(to.x(), to.y(), to.z()).addFaces(face -> new CubeBuilder.Face(face, BORDER_SPRITE)).bake());
        }
    }

    private static void addHollowCover(List<BakedQuad> quads, BlockState state, Direction coverSide, boolean hasUp, boolean hasDown, boolean hasEast, boolean hasWest, int size, RandomSource random) {
        AABB bounds = ConstantsCable.getCoverBounds(coverSide);

        float fromX = (float) bounds.minX * 16;
        float fromY = (float) bounds.minY * 16;
        float fromZ = (float) bounds.minZ * 16;

        float toX = (float) bounds.maxX * 16;
        float toY = (float) bounds.maxY * 16;
        float toZ = (float) bounds.maxZ * 16;

        if (coverSide.getAxis() != Direction.Axis.Y) {
            if (hasDown) {
                fromY = 2;
            }

            if (hasUp) {
                toY = 14;
            }
        }

        // Right
        if (coverSide == Direction.NORTH) {
            if (hasWest) {
                fromX = 2;
            } else {
                fromX = 0;
            }

            toX = size;
        } else if (coverSide == Direction.SOUTH) {
            if (hasEast) {
                toX = 14;
            } else {
                toX = 16;
            }

            fromX = 16F - size;
        } else if (coverSide == Direction.EAST) {
            fromZ = 0;
            toZ = size;
        } else if (coverSide == Direction.WEST) {
            fromZ = 16F - size;
            toZ = 16;
        } else if (coverSide == Direction.DOWN || coverSide == Direction.UP) {
            fromZ = 16F - size;
            toZ = 16;
        }
        HashMap<Direction, TextureAtlasSprite> spriteCache = new HashMap<>(); //Changed from 1.12: to improve sprite getting for each side
        quads.addAll(new CubeBuilder()
            .from(fromX, fromY, fromZ)
            .to(toX, toY, toZ)
            .addFaces(face -> new CubeBuilder.Face(face, spriteCache.computeIfAbsent(face, direction -> RenderUtils.getSprite(Minecraft.getInstance().getBlockRenderer().getBlockModel(state), state, direction, random))))
            .bake()
        );

        // Left
        if (coverSide == Direction.NORTH) {
            if (hasEast) {
                toX = 14;
            } else {
                toX = 16;
            }

            fromX = 16F - size;
        } else if (coverSide == Direction.SOUTH) {
            if (hasWest) {
                fromX = 2;
            } else {
                fromX = 0;
            }

            toX = size;
        } else if (coverSide == Direction.EAST) {
            fromZ = 16F - size;
            toZ = 16;
        } else if (coverSide == Direction.WEST) {
            fromZ = 0;
            toZ = size;
        } else if (coverSide == Direction.DOWN || coverSide == Direction.UP) {
            fromZ = 0;
            toZ = size;
        }

        quads.addAll(new CubeBuilder()
            .from(fromX, fromY, fromZ)
            .to(toX, toY, toZ)
            .addFaces(face -> new CubeBuilder.Face(face, spriteCache.computeIfAbsent(face, direction -> RenderUtils.getSprite(Minecraft.getInstance().getBlockRenderer().getBlockModel(state), state, direction, random))))
            .bake()
        );

        // Bottom
        if (coverSide == Direction.NORTH) {
            fromX = size;
            toX = 16F - size;

            if (hasDown) {
                fromY = 2;
            } else {
                fromY = 0;
            }

            toY = size;
        } else if (coverSide == Direction.SOUTH) {
            fromX = size;
            toX = 16F - size;

            if (hasDown) {
                fromY = 2;
            } else {
                fromY = 0;
            }

            toY = size;
        } else if (coverSide == Direction.EAST) {
            fromZ = size;
            toZ = 16F - size;

            if (hasDown) {
                fromY = 2;
            } else {
                fromY = 0;
            }

            toY = size;
        } else if (coverSide == Direction.WEST) {
            fromZ = size;
            toZ = 16F - size;

            if (hasDown) {
                fromY = 2;
            } else {
                fromY = 0;
            }

            toY = size;
        } else if (coverSide == Direction.DOWN || coverSide == Direction.UP) {
            fromZ = size;
            toZ = 16F - size;

            fromX = 0;
            toX = size;
        }

        quads.addAll(new CubeBuilder()
            .from(fromX, fromY, fromZ)
            .to(toX, toY, toZ)
            .addFaces(face -> new CubeBuilder.Face(face, spriteCache.computeIfAbsent(face, direction -> RenderUtils.getSprite(Minecraft.getInstance().getBlockRenderer().getBlockModel(state), state, direction, random))))
            .bake()
        );

        // Up
        if (coverSide == Direction.NORTH) {
            fromX = size;
            toX = 16F - size;

            if (hasUp) {
                toY = 14;
            } else {
                toY = 16;
            }

            fromY = 16F - size;
        } else if (coverSide == Direction.SOUTH) {
            fromX = size;
            toX = 16F - size;

            if (hasUp) {
                toY = 14;
            } else {
                toY = 16;
            }

            fromY = 16F - size;
        } else if (coverSide == Direction.EAST) {
            fromZ = size;
            toZ = 16F - size;

            if (hasUp) {
                toY = 14;
            } else {
                toY = 16;
            }

            fromY = 16F - size;
        } else if (coverSide == Direction.WEST) {
            fromZ = size;
            toZ = 16F - size;

            if (hasUp) {
                toY = 14;
            } else {
                toY = 16;
            }

            fromY = 16F - size;
        } else if (coverSide == Direction.DOWN || coverSide == Direction.UP) {
            fromZ = size;
            toZ = 16F - size;

            fromX = 16F - size;
            toX = 16;
        }

        quads.addAll(new CubeBuilder()
            .from(fromX, fromY, fromZ)
            .to(toX, toY, toZ)
            .addFaces(face -> new CubeBuilder.Face(face, spriteCache.computeIfAbsent(face, direction -> RenderUtils.getSprite(Minecraft.getInstance().getBlockRenderer().getBlockModel(state), state, direction, random))))
            .bake()
        );
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable final BlockState state,
                                    @Nullable final Direction side,
                                    @Nonnull final RandomSource rand,
                                    @Nonnull final ModelData extraData,
                                    @Nullable final RenderType renderType) {
        List<BakedQuad> quads = new ArrayList<>(super.getQuads(state, side, rand, extraData, renderType));
        if (extraData.has(CoverManager.PROPERTY)) {
            CoverManager manager = extraData.get(CoverManager.PROPERTY);
            addCover(quads, manager.getCover(Direction.NORTH), Direction.NORTH, side, rand, manager, state, true);
            addCover(quads, manager.getCover(Direction.SOUTH), Direction.SOUTH, side, rand, manager, state, true);
            addCover(quads, manager.getCover(Direction.EAST), Direction.EAST, side, rand, manager, state, true);
            addCover(quads, manager.getCover(Direction.WEST), Direction.WEST, side, rand, manager, state, true);
            addCover(quads, manager.getCover(Direction.DOWN), Direction.DOWN, side, rand, manager, state, true);
            addCover(quads, manager.getCover(Direction.UP), Direction.UP, side, rand, manager, state, true);
        }
        return quads;
    }
}

