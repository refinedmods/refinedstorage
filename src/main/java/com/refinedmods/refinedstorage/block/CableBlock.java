package com.refinedmods.refinedstorage.block;

import com.refinedmods.refinedstorage.api.network.node.ICoverable;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.network.node.INetworkNodeProxy;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.Cover;
import com.refinedmods.refinedstorage.apiimpl.network.node.cover.CoverType;
import com.refinedmods.refinedstorage.block.shape.ShapeCache;
import com.refinedmods.refinedstorage.capability.NetworkNodeProxyCapability;
import com.refinedmods.refinedstorage.render.ConstantsCable;
import com.refinedmods.refinedstorage.blockentity.CableBlockEntity;
import com.refinedmods.refinedstorage.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.Optional;

public class CableBlock extends NetworkNodeBlock implements SimpleWaterloggedBlock {
    protected static final VoxelShape HOLDER_NORTH = box(7, 7, 2, 9, 9, 6);
    protected static final VoxelShape HOLDER_EAST = box(10, 7, 7, 14, 9, 9);
    protected static final VoxelShape HOLDER_SOUTH = box(7, 7, 10, 9, 9, 14);
    protected static final VoxelShape HOLDER_WEST = box(2, 7, 7, 6, 9, 9);
    protected static final VoxelShape HOLDER_UP = box(7, 10, 7, 9, 14, 9);
    protected static final VoxelShape HOLDER_DOWN = box(7, 2, 7, 9, 6, 9);
    private static final BooleanProperty NORTH = BooleanProperty.create("north");
    private static final BooleanProperty EAST = BooleanProperty.create("east");
    private static final BooleanProperty SOUTH = BooleanProperty.create("south");
    private static final BooleanProperty WEST = BooleanProperty.create("west");
    private static final BooleanProperty UP = BooleanProperty.create("up");
    private static final BooleanProperty DOWN = BooleanProperty.create("down");
    private static final BooleanProperty WATERLOGGED = BooleanProperty.create("waterlogged");
    private static final VoxelShape SHAPE_CORE = box(6, 6, 6, 10, 10, 10);
    private static final VoxelShape SHAPE_NORTH = box(6, 6, 0, 10, 10, 6);
    private static final VoxelShape SHAPE_EAST = box(10, 6, 6, 16, 10, 10);
    private static final VoxelShape SHAPE_SOUTH = box(6, 6, 10, 10, 10, 16);
    private static final VoxelShape SHAPE_WEST = box(0, 6, 6, 6, 10, 10);
    private static final VoxelShape SHAPE_UP = box(6, 10, 6, 10, 16, 10);
    private static final VoxelShape SHAPE_DOWN = box(6, 0, 6, 10, 6, 10);

    public CableBlock(Properties props) {
        super(props);
        this.registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
    }

    public CableBlock() {
        super(BlockUtils.DEFAULT_GLASS_PROPERTIES);

        this.registerDefaultState(defaultBlockState().setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(UP, false).setValue(DOWN, false).setValue(WATERLOGGED, false));
    }

    protected static VoxelShape getCableShape(BlockState state) {
        VoxelShape shape = SHAPE_CORE;

        if (Boolean.TRUE.equals(state.getValue(NORTH))) {
            shape = Shapes.or(shape, SHAPE_NORTH);
        }

        if (Boolean.TRUE.equals(state.getValue(EAST))) {
            shape = Shapes.or(shape, SHAPE_EAST);
        }

        if (Boolean.TRUE.equals(state.getValue(SOUTH))) {
            shape = Shapes.or(shape, SHAPE_SOUTH);
        }

        if (Boolean.TRUE.equals(state.getValue(WEST))) {
            shape = Shapes.or(shape, SHAPE_WEST);
        }

        if (Boolean.TRUE.equals(state.getValue(UP))) {
            shape = Shapes.or(shape, SHAPE_UP);
        }

        if (Boolean.TRUE.equals(state.getValue(DOWN))) {
            shape = Shapes.or(shape, SHAPE_DOWN);
        }

        return shape;
    }

    public static boolean hasVisualConnectionOnSide(BlockState state, Direction direction) {
        switch (direction) {
            case DOWN:
                return state.getValue(DOWN);
            case UP:
                return state.getValue(UP);
            case NORTH:
                return state.getValue(NORTH);
            case SOUTH:
                return state.getValue(SOUTH);
            case WEST:
                return state.getValue(WEST);
            case EAST:
                return state.getValue(EAST);
        }
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState state, Direction dir, BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
        return getState(state, world, pos);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return ConstantsCable.addCoverVoxelShapes(ShapeCache.getOrCreate(state, CableBlock::getCableShape), world, pos);
    }

    @Override
    protected void onDirectionChanged(Level level, BlockPos pos, Direction newDirection) {
        // rotate() in BaseBlock "stupidly" changes the direction without checking if our cable connections are still valid.
        // You'd expect that cable connections are not changing when simply changing the direction.
        // But they need to. For example, when rotating a constructor to connect to a neighboring cable, the connection to that neighbor
        // needs to be removed as otherwise the "holder" of the constructor will conflict with the cable connection.
        // This is already checked in hasNode().
        // But since rotate() doesn't invalidate that connection, we need to do it here.
        // Ideally, this code would be in rotate(). But rotate() doesn't have any data about the position and world, so we need to do it here.
        level.setBlockAndUpdate(pos, getState(level.getBlockState(pos), level, pos));


        //when rotating skip rotations blocked by covers
        BlockDirection dir = getDirection();
        if (dir != BlockDirection.NONE) {
            if (isSideCovered(level.getBlockEntity(pos), newDirection)) {
                BlockState newState = rotate(level.getBlockState(pos), Rotation.CLOCKWISE_90);
                level.setBlockAndUpdate(pos, newState);
            }
        }

        super.onDirectionChanged(level, pos, newDirection);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        level.setBlockAndUpdate(pos, getState(level.getBlockState(pos), level, pos));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return getState(defaultBlockState(), ctx.getLevel(), ctx.getClickedPos());
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state) {
        return Boolean.TRUE.equals(state.getValue(WATERLOGGED)) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean placeLiquid(LevelAccessor worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {
        return SimpleWaterloggedBlock.super.placeLiquid(worldIn, pos, state, fluidStateIn);
    }

    @Override
    public boolean canPlaceLiquid(BlockGetter worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
        return SimpleWaterloggedBlock.super.canPlaceLiquid(worldIn, pos, state, fluidIn);
    }

    private boolean hasNodeConnection(LevelAccessor world, BlockPos pos, BlockState state, Direction direction) {
        // Prevent the "holder" of a cable block conflicting with a cable connection.
        if (getDirection() != BlockDirection.NONE && state.getValue(getDirection().getProperty()).getOpposite() == direction) {
            return false;
        }

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null) {
            return false;
        }

        return blockEntity.getCapability(NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY, direction).isPresent()
            && !isSideCovered(blockEntity, direction)
            && !isSideCovered(world.getBlockEntity(pos.relative(direction)), direction.getOpposite());
    }

    private boolean isSideCovered(BlockEntity blockEntity, Direction direction) {
        if (blockEntity == null) {
            return false;
        }

        Optional<INetworkNode> node = blockEntity.getCapability(NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY, direction).map(INetworkNodeProxy::getNode);

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

    private BlockState getState(BlockState currentState, LevelAccessor world, BlockPos pos) {
        boolean north = hasNodeConnection(world, pos.relative(Direction.NORTH), currentState, Direction.SOUTH);
        boolean east = hasNodeConnection(world, pos.relative(Direction.EAST), currentState, Direction.WEST);
        boolean south = hasNodeConnection(world, pos.relative(Direction.SOUTH), currentState, Direction.NORTH);
        boolean west = hasNodeConnection(world, pos.relative(Direction.WEST), currentState, Direction.EAST);
        boolean up = hasNodeConnection(world, pos.relative(Direction.UP), currentState, Direction.DOWN);
        boolean down = hasNodeConnection(world, pos.relative(Direction.DOWN), currentState, Direction.UP);

        return currentState
            .setValue(NORTH, north)
            .setValue(EAST, east)
            .setValue(SOUTH, south)
            .setValue(WEST, west)
            .setValue(UP, up)
            .setValue(DOWN, down);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CableBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);

        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, WATERLOGGED);
    }
}
