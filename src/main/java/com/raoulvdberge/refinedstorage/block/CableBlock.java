package com.raoulvdberge.refinedstorage.block;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.capability.NetworkNodeProxyCapability;
import com.raoulvdberge.refinedstorage.tile.CableTile;
import com.raoulvdberge.refinedstorage.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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

public class CableBlock extends NetworkNodeBlock {
    private static final BooleanProperty NORTH = BooleanProperty.create("north");
    private static final BooleanProperty EAST = BooleanProperty.create("east");
    private static final BooleanProperty SOUTH = BooleanProperty.create("south");
    private static final BooleanProperty WEST = BooleanProperty.create("west");
    private static final BooleanProperty UP = BooleanProperty.create("up");
    private static final BooleanProperty DOWN = BooleanProperty.create("down");

    protected static final VoxelShape HOLDER_NORTH = makeCuboidShape(7, 7, 2, 9, 9, 6);
    protected static final VoxelShape HOLDER_EAST = makeCuboidShape(10, 7, 7, 14, 9, 9);
    protected static final VoxelShape HOLDER_SOUTH = makeCuboidShape(7, 7, 10, 9, 9, 14);
    protected static final VoxelShape HOLDER_WEST = makeCuboidShape(2, 7, 7, 6, 9, 9);
    protected static final VoxelShape HOLDER_UP = makeCuboidShape(7, 10, 7, 9, 14, 9);
    protected static final VoxelShape HOLDER_DOWN = makeCuboidShape(7, 2, 7, 9, 6, 9);

    private static final VoxelShape SHAPE_CORE = makeCuboidShape(6, 6, 6, 10, 10, 10);
    private static final VoxelShape SHAPE_NORTH = makeCuboidShape(6, 6, 0, 10, 10, 6);
    private static final VoxelShape SHAPE_EAST = makeCuboidShape(10, 6, 6, 16, 10, 10);
    private static final VoxelShape SHAPE_SOUTH = makeCuboidShape(6, 6, 10, 10, 10, 16);
    private static final VoxelShape SHAPE_WEST = makeCuboidShape(0, 6, 6, 6, 10, 10);
    private static final VoxelShape SHAPE_UP = makeCuboidShape(6, 10, 6, 10, 16, 10);
    private static final VoxelShape SHAPE_DOWN = makeCuboidShape(6, 0, 6, 10, 6, 10);

    public CableBlock(Properties props) {
        super(props);
    }

    public CableBlock() {
        super(BlockUtils.DEFAULT_GLASS_PROPERTIES);

        this.setRegistryName(RS.ID, "cable");
        this.setDefaultState(getDefaultState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(UP, false).with(DOWN, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, block, fromPos, isMoving);

        world.setBlockState(pos, getState(state, world, pos));
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
        return getState(getDefaultState(), ctx.getWorld(), ctx.getPos());
    }

    private boolean hasNode(World world, BlockPos pos, BlockState state, Direction direction) {
        if (getDirection() != BlockDirection.NONE && state.get(getDirection().getProperty()) == direction.getOpposite()) {
            return false;
        }

        TileEntity tile = world.getTileEntity(pos);
        if (tile == null) {
            return false;
        }

        return tile.getCapability(NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY, direction).isPresent();
    }

    private BlockState getState(BlockState currentState, World world, BlockPos pos) {
        boolean north = hasNode(world, pos.offset(Direction.NORTH), currentState, Direction.SOUTH);
        boolean east = hasNode(world, pos.offset(Direction.EAST), currentState, Direction.WEST);
        boolean south = hasNode(world, pos.offset(Direction.SOUTH), currentState, Direction.NORTH);
        boolean west = hasNode(world, pos.offset(Direction.WEST), currentState, Direction.EAST);
        boolean up = hasNode(world, pos.offset(Direction.UP), currentState, Direction.DOWN);
        boolean down = hasNode(world, pos.offset(Direction.DOWN), currentState, Direction.UP);

        return currentState
            .with(NORTH, north)
            .with(EAST, east)
            .with(SOUTH, south)
            .with(WEST, west)
            .with(UP, up)
            .with(DOWN, down);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new CableTile();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);

        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}
