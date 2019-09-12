package com.raoulvdberge.refinedstorage.render.model.baked;

/*
public class BakedModelCableCover extends BakedModelDelegate {
    private static TextureAtlasSprite BORDER_SPRITE;

    public BakedModelCableCover(IBakedModel base) {
        super(base);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, long rand) {
        List<BakedQuad> quads = new ArrayList<>(base.getQuads(state, side, rand));

        if (state != null) {
            IExtendedBlockState extendedState = (IExtendedBlockState) state;

            addCover(quads, extendedState.getValue(BlockCable.COVER_NORTH), Direction.NORTH, side, rand, extendedState, true);
            addCover(quads, extendedState.getValue(BlockCable.COVER_SOUTH), Direction.SOUTH, side, rand, extendedState, true);
            addCover(quads, extendedState.getValue(BlockCable.COVER_EAST), Direction.EAST, side, rand, extendedState, true);
            addCover(quads, extendedState.getValue(BlockCable.COVER_WEST), Direction.WEST, side, rand, extendedState, true);
            addCover(quads, extendedState.getValue(BlockCable.COVER_DOWN), Direction.DOWN, side, rand, extendedState, true);
            addCover(quads, extendedState.getValue(BlockCable.COVER_UP), Direction.UP, side, rand, extendedState, true);
        }

        return quads;
    }

    private static int getHollowCoverSize(@Nullable BlockState state, Direction coverSide) {
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

    protected static void addCover(List<BakedQuad> quads, @Nullable Cover cover, Direction coverSide, Direction side, long rand, @Nullable IExtendedBlockState state, boolean handle) {
        if (cover == null) {
            return;
        }

        BlockState coverState = CoverManager.getBlockState(cover.getStack());

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

    private static void addNormalCover(List<BakedQuad> quads, TextureAtlasSprite sprite, Direction coverSide, boolean hasUp, boolean hasDown, boolean hasEast, boolean hasWest, boolean handle) {
        AxisAlignedBB bounds = ConstantsCable.getCoverBounds(coverSide);

        Vector3f from = new Vector3f((float) bounds.minX * 16, (float) bounds.minY * 16, (float) bounds.minZ * 16);
        Vector3f to = new Vector3f((float) bounds.maxX * 16, (float) bounds.maxY * 16, (float) bounds.maxZ * 16);

        if (coverSide == Direction.NORTH) {
            if (hasWest) {
                from.setX(2);
            }

            if (hasEast) {
                to.setX(14);
            }
        } else if (coverSide == Direction.SOUTH) {
            if (hasWest) {
                from.setX(2);
            }

            if (hasEast) {
                to.setX(14);
            }
        }

        if (coverSide.getAxis() != Direction.Axis.Y) {
            if (hasDown) {
                from.setY(2);
            }

            if (hasUp) {
                to.setY(14);
            }
        }

        quads.addAll(new CubeBuilder().from(from.getX(), from.getY(), from.getZ()).to(to.getX(), to.getY(), to.getZ()).addFaces(face -> new CubeBuilder.Face(face, sprite)).bake());

        if (handle) {
            if (BORDER_SPRITE == null) {
                BORDER_SPRITE = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(RS.ID + ":blocks/cable_part_border");
            }

            bounds = ConstantsCable.getHolderBounds(coverSide);

            from = new Vector3f((float) bounds.minX * 16, (float) bounds.minY * 16, (float) bounds.minZ * 16);
            to = new Vector3f((float) bounds.maxX * 16, (float) bounds.maxY * 16, (float) bounds.maxZ * 16);

            quads.addAll(new CubeBuilder().from(from.getX(), from.getY(), from.getZ()).to(to.getX(), to.getY(), to.getZ()).addFaces(face -> new CubeBuilder.Face(face, BORDER_SPRITE)).bake());
        }
    }

    private static void addHollowCover(List<BakedQuad> quads, TextureAtlasSprite sprite, Direction coverSide, boolean hasUp, boolean hasDown, boolean hasEast, boolean hasWest, int size) {
        AxisAlignedBB bounds = ConstantsCable.getCoverBounds(coverSide);

        Vector3f from = new Vector3f((float) bounds.minX * 16, (float) bounds.minY * 16, (float) bounds.minZ * 16);
        Vector3f to = new Vector3f((float) bounds.maxX * 16, (float) bounds.maxY * 16, (float) bounds.maxZ * 16);

        if (coverSide.getAxis() != Direction.Axis.Y) {
            if (hasDown) {
                from.setY(2);
            }

            if (hasUp) {
                to.setY(14);
            }
        }

        // Right
        if (coverSide == Direction.NORTH) {
            if (hasWest) {
                from.setX(2);
            } else {
                from.setX(0);
            }

            to.setX(size);
        } else if (coverSide == Direction.SOUTH) {
            if (hasEast) {
                to.setX(14);
            } else {
                to.setX(16);
            }

            from.setX(16 - size);
        } else if (coverSide == Direction.EAST) {
            from.setZ(0);
            to.setZ(size);
        } else if (coverSide == Direction.WEST) {
            from.setZ(16 - size);
            to.setZ(16);
        } else if (coverSide == Direction.DOWN || coverSide == Direction.UP) {
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
        if (coverSide == Direction.NORTH) {
            if (hasEast) {
                to.setX(14);
            } else {
                to.setX(16);
            }

            from.setX(16 - size);
        } else if (coverSide == Direction.SOUTH) {
            if (hasWest) {
                from.setX(2);
            } else {
                from.setX(0);
            }

            to.setX(size);
        } else if (coverSide == Direction.EAST) {
            from.setZ(16 - size);
            to.setZ(16);
        } else if (coverSide == Direction.WEST) {
            from.setZ(0);
            to.setZ(size);
        } else if (coverSide == Direction.DOWN || coverSide == Direction.UP) {
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
        if (coverSide == Direction.NORTH) {
            from.setX(size);
            to.setX(16 - size);

            if (hasDown) {
                from.setY(2);
            } else {
                from.setY(0);
            }

            to.setY(size);
        } else if (coverSide == Direction.SOUTH) {
            from.setX(size);
            to.setX(16 - size);

            if (hasDown) {
                from.setY(2);
            } else {
                from.setY(0);
            }

            to.setY(size);
        } else if (coverSide == Direction.EAST) {
            from.setZ(size);
            to.setZ(16 - size);

            if (hasDown) {
                from.setY(2);
            } else {
                from.setY(0);
            }

            to.setY(size);
        } else if (coverSide == Direction.WEST) {
            from.setZ(size);
            to.setZ(16 - size);

            if (hasDown) {
                from.setY(2);
            } else {
                from.setY(0);
            }

            to.setY(size);
        } else if (coverSide == Direction.DOWN || coverSide == Direction.UP) {
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
        if (coverSide == Direction.NORTH) {
            from.setX(size);
            to.setX(16 - size);

            if (hasUp) {
                to.setY(14);
            } else {
                to.setY(16);
            }

            from.setY(16 - size);
        } else if (coverSide == Direction.SOUTH) {
            from.setX(size);
            to.setX(16 - size);

            if (hasUp) {
                to.setY(14);
            } else {
                to.setY(16);
            }

            from.setY(16 - size);
        } else if (coverSide == Direction.EAST) {
            from.setZ(size);
            to.setZ(16 - size);

            if (hasUp) {
                to.setY(14);
            } else {
                to.setY(16);
            }

            from.setY(16 - size);
        } else if (coverSide == Direction.WEST) {
            from.setZ(size);
            to.setZ(16 - size);

            if (hasUp) {
                to.setY(14);
            } else {
                to.setY(16);
            }

            from.setY(16 - size);
        } else if (coverSide == Direction.DOWN || coverSide == Direction.UP) {
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
*/