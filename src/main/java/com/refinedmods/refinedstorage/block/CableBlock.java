package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.api.network.node.ICoverable;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.Cover;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.CoverType;
import com.refinedmods.refinedstorage.block.shape.ShapeCache;
import com.refinedmods.refinedstorage.capability.NetworkNodeProxyCapability;
import com.refinedmods.refinedstorage.render.ConstantsCable;
import com.refinedmods.refinedstorage.tile.CableTile;
import com.refinedmods.refinedstorage.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Optional;

public class CableBlock extends NetworkNodeBlock implements IWaterLoggable {
    private static final BooleanProperty NORTH = BooleanProperty.create("north");
    private static final BooleanProperty EAST = BooleanProperty.create("east");
    private static final BooleanProperty SOUTH = BooleanProperty.create("south");
    private static final BooleanProperty WEST = BooleanProperty.create("west");
    private static final BooleanProperty UP = BooleanProperty.create("up");
    private static final BooleanProperty DOWN = BooleanProperty.create("down");
    private static final BooleanProperty WATERLOGGED = BooleanProperty.create("waterlogged");

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
        this.setDefaultState(getDefaultState().with(WATERLOGGED, false));
    }

    public CableBlock() {
        super(BlockUtils.DEFAULT_GLASS_PROPERTIES);

        this.setDefaultState(getDefaultState().with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(UP, false).with(DOWN, false).with(WATERLOGGED, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updatePostPlacement(BlockState state, Direction dir, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos) {
        return getState(state, world, pos);
    }

    @Override
    public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
        return ConstantsCable.addCoverVoxelShapes(ShapeCache.getOrCreate(state, CableBlock::getCableShape), world, pos);
    }

    protected static VoxelShape getCableShape(BlockState state) {
        VoxelShape shape = SHAPE_CORE;

        if (Boolean.TRUE.equals(state.get(NORTH))) {
            shape = VoxelShapes.or(shape, SHAPE_NORTH);
        }

        if (Boolean.TRUE.equals(state.get(EAST))) {
            shape = VoxelShapes.or(shape, SHAPE_EAST);
        }

        if (Boolean.TRUE.equals(state.get(SOUTH))) {
            shape = VoxelShapes.or(shape, SHAPE_SOUTH);
        }

        if (Boolean.TRUE.equals(state.get(WEST))) {
            shape = VoxelShapes.or(shape, SHAPE_WEST);
        }

        if (Boolean.TRUE.equals(state.get(UP))) {
            shape = VoxelShapes.or(shape, SHAPE_UP);
        }

        if (Boolean.TRUE.equals(state.get(DOWN))) {
            shape = VoxelShapes.or(shape, SHAPE_DOWN);
        }

        return shape;
    }

    @Override
    protected void onDirectionChanged(World world, BlockPos pos, Direction newDirection) {
        // rotate() in BaseBlock "stupidly" changes the direction without checking if our cable connections are still valid.
        // You'd expect that cable connections are not changing when simply changing the direction.
        // But they need to. For example, when rotating a constructor to connect to a neighboring cable, the connection to that neighbor
        // needs to be removed as otherwise the "holder" of the constructor will conflict with the cable connection.
        // This is already checked in hasNode().
        // But since rotate() doesn't invalidate that connection, we need to do it here.
        // Ideally, this code would be in rotate(). But rotate() doesn't have any data about the position and world, so we need to do it here.
        world.setBlockState(pos, getState(world.getBlockState(pos), world, pos));


        //when rotating skip rotations blocked by covers
        BlockDirection dir = getDirection();
        if (dir != BlockDirection.NONE) {
            if (isSideCovered(world.getTileEntity(pos), newDirection)) {
                BlockState newState = rotate(world.getBlockState(pos), Rotation.CLOCKWISE_90);
                world.setBlockState(pos, newState);
            }
        }

        super.onDirectionChanged(world, pos, newDirection);
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, world, pos, blockIn, fromPos, isMoving);
        world.setBlockState(pos, getState(world.getBlockState(pos), world, pos));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext ctx) {
        return getState(getDefaultState(), ctx.getWorld(), ctx.getPos());
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state) {
        return Boolean.TRUE.equals(state.get(WATERLOGGED)) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    public boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {
        return IWaterLoggable.super.receiveFluid(worldIn, pos, state, fluidStateIn);
    }

    @Override
    public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
        return IWaterLoggable.super.canContainFluid(worldIn, pos, state, fluidIn);
    }

    private boolean hasNodeConnection(IWorld world, BlockPos pos, BlockState state, Direction direction) {
        // Prevent the "holder" of a cable block conflicting with a cable connection.
        if (getDirection() != BlockDirection.NONE && state.get(getDirection().getProperty()).getOpposite() == direction) {
            return false;
        }

        TileEntity tile = world.getTileEntity(pos);
        if (tile == null) {
            return false;
        }

        return tile.getCapability(NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY, direction).isPresent()
            && !isSideCovered(tile, direction)
            && !isSideCovered(world.getTileEntity(pos.offset(direction)), direction.getOpposite());
    }

    private boolean isSideCovered(TileEntity tile, Direction direction) {
        if (tile == null) {
            return false;
        }

        Optional<INetworkNode> node = tile.getCapability(NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY, direction).map(INetworkNodeProxy::getNode);

        if (node.isPresent() && node.get() instanceof ICoverable) {
            Cover cover = ((ICoverable) node.get()).getCoverManager().getCover(direction);
            if (cover == null) {
                return false;
            } else {
                return cover.getType() == CoverType.NORMAL;
            }
        }

        return false;
    }

    private BlockState getState(BlockState currentState, IWorld world, BlockPos pos) {
        boolean north = hasNodeConnection(world, pos.offset(Direction.NORTH), currentState, Direction.SOUTH);
        boolean east = hasNodeConnection(world, pos.offset(Direction.EAST), currentState, Direction.WEST);
        boolean south = hasNodeConnection(world, pos.offset(Direction.SOUTH), currentState, Direction.NORTH);
        boolean west = hasNodeConnection(world, pos.offset(Direction.WEST), currentState, Direction.EAST);
        boolean up = hasNodeConnection(world, pos.offset(Direction.UP), currentState, Direction.DOWN);
        boolean down = hasNodeConnection(world, pos.offset(Direction.DOWN), currentState, Direction.UP);

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

        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, WATERLOGGED);
    }

    public static boolean hasVisualConnectionOnSide(BlockState state, Direction direction) {
        switch (direction) {
            case DOWN:
                return state.get(DOWN);
            case UP:
                return state.get(UP);
            case NORTH:
                return state.get(NORTH);
            case SOUTH:
                return state.get(SOUTH);
            case WEST:
                return state.get(WEST);
            case EAST:
                return state.get(EAST);
        }
        return false;
    }
}
