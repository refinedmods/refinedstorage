package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.capability.NetworkNodeProxyCapability;
import com.raoulvdberge.refinedstorage.tile.CableTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class CableBlock extends NodeBlock {
    /* TODO
    public static final PropertyObject<Cover> COVER_NORTH = new PropertyObject<>("cover_north", Cover.class);
    public static final PropertyObject<Cover> COVER_EAST = new PropertyObject<>("cover_east", Cover.class);
    public static final PropertyObject<Cover> COVER_SOUTH = new PropertyObject<>("cover_south", Cover.class);
    public static final PropertyObject<Cover> COVER_WEST = new PropertyObject<>("cover_west", Cover.class);
    public static final PropertyObject<Cover> COVER_UP = new PropertyObject<>("cover_up", Cover.class);
    public static final PropertyObject<Cover> COVER_DOWN = new PropertyObject<>("cover_down", Cover.class);*/

    private static final BooleanProperty NORTH = BooleanProperty.create("north");
    private static final BooleanProperty EAST = BooleanProperty.create("east");
    private static final BooleanProperty SOUTH = BooleanProperty.create("south");
    private static final BooleanProperty WEST = BooleanProperty.create("west");
    private static final BooleanProperty UP = BooleanProperty.create("up");
    private static final BooleanProperty DOWN = BooleanProperty.create("down");

    private static final VoxelShape SHAPE_CORE = makeCuboidShape(6, 6, 6, 10, 10, 10);
    private static final VoxelShape SHAPE_NORTH = makeCuboidShape(6, 6, 0, 10, 10, 6);
    private static final VoxelShape SHAPE_EAST = makeCuboidShape(10, 6, 6, 16, 10, 10);
    private static final VoxelShape SHAPE_SOUTH = makeCuboidShape(6, 6, 10, 10, 10, 16);
    private static final VoxelShape SHAPE_WEST = makeCuboidShape(0, 6, 6, 6, 10, 10);
    private static final VoxelShape SHAPE_UP = makeCuboidShape(6, 10, 6, 10, 16, 10);
    private static final VoxelShape SHAPE_DOWN = makeCuboidShape(6, 0, 6, 10, 6, 10);

    public CableBlock() {
        super(Block.Properties.create(Material.GLASS).sound(SoundType.GLASS).hardnessAndResistance(0.35F));

        this.setRegistryName(RS.ID, "cable");
        this.setDefaultState(getDefaultState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(UP, false).with(DOWN, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);

        world.setBlockState(pos, getState(world, pos));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
        VoxelShape shape = SHAPE_CORE;

        if (state.get(NORTH)) {
            shape = VoxelShapes.or(shape, SHAPE_NORTH);
        }

        if (state.get(EAST)) {
            shape = VoxelShapes.or(shape, SHAPE_EAST);
        }

        if (state.get(SOUTH)) {
            shape = VoxelShapes.or(shape, SHAPE_SOUTH);
        }

        if (state.get(WEST)) {
            shape = VoxelShapes.or(shape, SHAPE_WEST);
        }

        if (state.get(UP)) {
            shape = VoxelShapes.or(shape, SHAPE_UP);
        }

        if (state.get(DOWN)) {
            shape = VoxelShapes.or(shape, SHAPE_DOWN);
        }

        return shape;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext ctx) {
        return getState(ctx.getWorld(), ctx.getPos());
    }

    private static boolean hasNode(World world, BlockPos pos, Direction direction) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile == null) {
            return false;
        }

        return tile.getCapability(NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY, direction).isPresent();
    }

    private BlockState getState(World world, BlockPos pos) {
        boolean north = hasNode(world, pos.offset(Direction.NORTH), Direction.SOUTH);
        boolean east = hasNode(world, pos.offset(Direction.EAST), Direction.WEST);
        boolean south = hasNode(world, pos.offset(Direction.SOUTH), Direction.NORTH);
        boolean west = hasNode(world, pos.offset(Direction.WEST), Direction.EAST);
        boolean up = hasNode(world, pos.offset(Direction.UP), Direction.DOWN);
        boolean down = hasNode(world, pos.offset(Direction.DOWN), Direction.UP);

        return getDefaultState()
            .with(NORTH, north)
            .with(EAST, east)
            .with(SOUTH, south)
            .with(WEST, west)
            .with(UP, up)
            .with(DOWN, down);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new CableTile();
    }

    /* TODO
    @OnlyIn(Dist.CLIENT)
    void registerCover(IModelRegistration modelRegistration) {
        modelRegistration.addBakedModelOverride(info.getId(), BakedModelCableCover::new);
    }

    @OnlyIn(Dist.CLIENT)
    void registerCoverAndFullbright(IModelRegistration modelRegistration, ResourceLocation... textures) {
        modelRegistration.addBakedModelOverride(info.getId(), base -> new BakedModelCableCover(new BakedModelFullbright(base, textures)));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerModels(IModelRegistration modelRegistration) {
        modelRegistration.setModel(this, 0, new ModelResourceLocation(info.getId(), "down=false,east=true,north=false,south=false,up=false,west=true"));

        registerCover(modelRegistration);
    }*/

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);

        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    /* TODO
    @Override
    public BlockState getExtendedState(BlockState state, IBlockAccess world, BlockPos pos) {
        BlockState s = super.getExtendedState(state, world, pos);

        TileEntity tile = world.getTileEntity(pos);

        if (tile instanceof TileNode && ((TileNode) tile).getNode() instanceof ICoverable) {
            s = ((IExtendedBlockState) s).withProperty(COVER_NORTH, ((ICoverable) ((TileNode) tile).getNode()).getCoverManager().getCover(Direction.NORTH));
            s = ((IExtendedBlockState) s).withProperty(COVER_EAST, ((ICoverable) ((TileNode) tile).getNode()).getCoverManager().getCover(Direction.EAST));
            s = ((IExtendedBlockState) s).withProperty(COVER_SOUTH, ((ICoverable) ((TileNode) tile).getNode()).getCoverManager().getCover(Direction.SOUTH));
            s = ((IExtendedBlockState) s).withProperty(COVER_WEST, ((ICoverable) ((TileNode) tile).getNode()).getCoverManager().getCover(Direction.WEST));
            s = ((IExtendedBlockState) s).withProperty(COVER_UP, ((ICoverable) ((TileNode) tile).getNode()).getCoverManager().getCover(Direction.UP));
            s = ((IExtendedBlockState) s).withProperty(COVER_DOWN, ((ICoverable) ((TileNode) tile).getNode()).getCoverManager().getCover(Direction.DOWN));
        }

        return s;
    }*/

    /* TODO
    private static boolean hasConnectionWith(World world, BlockPos pos, BlockBase block, TileEntity tile, Direction direction) {
        if (!(tile instanceof TileNode)) {
            return false;
        }

        INetworkNode node = ((TileNode) tile).getNode();

        if (node instanceof ICoverable) {
            Cover cover = ((ICoverable) node).getCoverManager().getCover(direction);

            if (cover != null && cover.getType() != CoverType.HOLLOW) {
                return false;
            }
        }

        TileEntity otherTile = world.getTileEntity(pos.offset(direction));

        if (otherTile instanceof TileNode && ((TileNode) otherTile).getNode() instanceof ICoverable) {
            Cover cover = ((ICoverable) ((TileNode) otherTile).getNode()).getCoverManager().getCover(direction.getOpposite());

            if (cover != null && cover.getType() != CoverType.HOLLOW) {
                return false;
            }
        }

        if (otherTile != null && otherTile.hasCapability(CapabilityNetworkNodeProxy.NETWORK_NODE_PROXY_CAPABILITY, direction.getOpposite())) {
            // Prevent the block adding connections in itself
            // For example: importer cable connection on the importer face
            if (block.getDirection() != null && ((TileBase) tile).getDirection() == direction) {
                return false;
            }

            return true;
        }

        return false;
    }*/

    /* TODO
    @Override
    public List<CollisionGroup> getCollisions(TileEntity tile, BlockState state) {
        List<CollisionGroup> groups = getCoverCollisions(tile);

        groups.add(ConstantsCable.CORE);

        if (state.getValue(NORTH)) {
            groups.add(ConstantsCable.NORTH);
        }

        if (state.getValue(EAST)) {
            groups.add(ConstantsCable.EAST);
        }

        if (state.getValue(SOUTH)) {
            groups.add(ConstantsCable.SOUTH);
        }

        if (state.getValue(WEST)) {
            groups.add(ConstantsCable.WEST);
        }

        if (state.getValue(UP)) {
            groups.add(ConstantsCable.UP);
        }

        if (state.getValue(DOWN)) {
            groups.add(ConstantsCable.DOWN);
        }

        return groups;
    }*/

    /* TODO
    private List<CollisionGroup> getCoverCollisions(TileEntity tile) {
        List<CollisionGroup> groups = new ArrayList<>();

        if (tile instanceof TileNode && ((TileNode) tile).getNode() instanceof ICoverable) {
            CoverManager coverManager = ((ICoverable) ((TileNode) tile).getNode()).getCoverManager();

            Cover coverNorth = coverManager.getCover(Direction.NORTH);
            Cover coverEast = coverManager.getCover(Direction.EAST);
            Cover coverSouth = coverManager.getCover(Direction.SOUTH);
            Cover coverWest = coverManager.getCover(Direction.WEST);
            Cover coverUp = coverManager.getCover(Direction.UP);
            Cover coverDown = coverManager.getCover(Direction.DOWN);

            if (coverNorth != null) {
                groups.add(new CollisionGroup().addItem(CollisionUtils.getBounds(
                    coverWest != null ? 2 : 0, coverDown != null ? 2 : 0, 0,
                    coverEast != null ? 14 : 16, coverUp != null ? 14 : 16, 2
                )).setDirection(Direction.NORTH));

                if (coverNorth.getType() != CoverType.HOLLOW) {
                    groups.add(ConstantsCable.HOLDER_NORTH);
                }
            }

            if (coverEast != null) {
                groups.add(new CollisionGroup().addItem(CollisionUtils.getBounds(
                    14, coverDown != null ? 2 : 0, 0,
                    16, coverUp != null ? 14 : 16, 16
                )).setDirection(Direction.EAST));

                if (coverEast.getType() != CoverType.HOLLOW) {
                    groups.add(ConstantsCable.HOLDER_EAST);
                }
            }

            if (coverSouth != null) {
                groups.add(new CollisionGroup().addItem(CollisionUtils.getBounds(
                    coverEast != null ? 14 : 16, coverDown != null ? 2 : 0, 16,
                    coverWest != null ? 2 : 0, coverUp != null ? 14 : 16, 14
                )).setDirection(Direction.SOUTH));

                if (coverSouth.getType() != CoverType.HOLLOW) {
                    groups.add(ConstantsCable.HOLDER_SOUTH);
                }
            }

            if (coverWest != null) {
                groups.add(new CollisionGroup().addItem(CollisionUtils.getBounds(
                    0, coverDown != null ? 2 : 0, 0,
                    2, coverUp != null ? 14 : 16, 16
                )).setDirection(Direction.WEST));

                if (coverWest.getType() != CoverType.HOLLOW) {
                    groups.add(ConstantsCable.HOLDER_WEST);
                }
            }

            if (coverUp != null) {
                groups.add(new CollisionGroup().addItem(CollisionUtils.getBounds(
                    0, 14, 0,
                    16, 16, 16
                )).setDirection(Direction.UP));

                if (coverUp.getType() != CoverType.HOLLOW) {
                    groups.add(ConstantsCable.HOLDER_UP);
                }
            }

            if (coverDown != null) {
                groups.add(new CollisionGroup().addItem(CollisionUtils.getBounds(
                    0, 0, 0,
                    16, 2, 16
                )).setDirection(Direction.DOWN));

                if (coverDown.getType() != CoverType.HOLLOW) {
                    groups.add(ConstantsCable.HOLDER_DOWN);
                }
            }
        }

        return groups;
    }*/

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}
