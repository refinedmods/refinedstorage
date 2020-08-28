package com.refinedmods.refinedstorage.block

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.RSComponents
import com.refinedmods.refinedstorage.block.shape.ShapeCache.getOrCreate
import com.refinedmods.refinedstorage.extensions.getCustomLogger
import com.refinedmods.refinedstorage.tile.CableTile
import com.refinedmods.refinedstorage.util.BlockUtils
import com.thinkslynk.fabric.annotations.registry.RegisterBlock
import com.thinkslynk.fabric.annotations.registry.RegisterBlockItem
import dev.onyxstudios.cca.api.v3.block.BlockComponents
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import net.minecraft.world.WorldAccess
import java.util.function.Function

@RegisterBlock(RS.ID, CableBlock.ID)
@RegisterBlockItem(RS.ID, CableBlock.ID, "CURED_STORAGE")
open class CableBlock( settingsIn: Settings = BlockUtils.DEFAULT_GLASS_PROPERTIES):
        NetworkNodeBlock(settingsIn),
        BlockEntityProvider
{
    init {
        defaultState = defaultState
                .with(NORTH, false)
                .with(EAST, false)
                .with(SOUTH, false)
                .with(WEST, false)
                .with(UP, false)
                .with(DOWN, false)
                .with(WATERLOGGED, false)
    }

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        return getOrCreate(state, Function { getCableShape(it) })
    }

    override fun onDirectionChanged(world: World, pos: BlockPos, newDirection: Direction) {
        // rotate() in BaseBlock "stupidly" changes the direction without checking if our cable connections are still valid.
        // You'd expect that cable connections are not changing when simply changing the direction.
        // But they need to. For example, when rotating a constructor to connect to a neighboring cable, the connection to that neighbor
        // needs to be removed as otherwise the "holder" of the constructor will conflict with the cable connection.
        // This is already checked in hasNode().
        // But since rotate() doesn't invalidate that connection, we need to do it here.
        // Ideally, this code would be in rotate(). But rotate() doesn't have any data about the position and world, so we need to do it here.
        world.setBlockState(pos, getState(world.getBlockState(pos), world, pos))
        super.onDirectionChanged(world, pos, newDirection)
    }

    override fun getStateForNeighborUpdate(state: BlockState, direction: Direction, newState: BlockState, world: WorldAccess, pos: BlockPos, posFrom: BlockPos): BlockState {
        return getState(defaultState, world, pos)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        return getState(defaultState, ctx.world, ctx.blockPos)
    }

//    override fun getFluidState(state: BlockState): FluidState {
//        return when {
//            state.get(WATERLOGGED) -> Fluids.WATER.getStill(false)
//            else -> super.getFluidState(state)
//        }
//    }

    @Suppress("UnstableApiUsage")
    private fun hasNode(world: BlockView, pos: BlockPos, state: BlockState, direction: Direction): Boolean {
        // Prevent the "holder" of a cable block conflicting with a cable connection.
        if (this.direction != BlockDirection.NONE && state.get<Direction>(this.direction.property).opposite == direction) {
            return false
        }

        return BlockComponents.get(RSComponents.NETWORK_NODE_PROXY, world, pos, direction) != null
    }


    private fun getState(currentState: BlockState, world: BlockView, pos: BlockPos): BlockState {
        val north = hasNode(world, pos.offset(Direction.NORTH), currentState, Direction.SOUTH)
        val east = hasNode(world, pos.offset(Direction.EAST), currentState, Direction.WEST)
        val south = hasNode(world, pos.offset(Direction.SOUTH), currentState, Direction.NORTH)
        val west = hasNode(world, pos.offset(Direction.WEST), currentState, Direction.EAST)
        val up = hasNode(world, pos.offset(Direction.UP), currentState, Direction.DOWN)
        val down = hasNode(world, pos.offset(Direction.DOWN), currentState, Direction.UP)
        return currentState
                .with(NORTH, north)
                .with(EAST, east)
                .with(SOUTH, south)
                .with(WEST, west)
                .with(UP, up)
                .with(DOWN, down)
    }

    override fun createBlockEntity(world: BlockView): BlockEntity {
        return CableTile()
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, WATERLOGGED)
    }

    companion object {
        val log = getCustomLogger(ConstructorBlock::class)
        const val ID = "cable"

        private val NORTH: BooleanProperty = BooleanProperty.of("north")
        private val EAST: BooleanProperty = BooleanProperty.of("east")
        private val SOUTH: BooleanProperty = BooleanProperty.of("south")
        private val WEST: BooleanProperty = BooleanProperty.of("west")
        private val UP: BooleanProperty = BooleanProperty.of("up")
        private val DOWN: BooleanProperty = BooleanProperty.of("down")
        private val WATERLOGGED: BooleanProperty = BooleanProperty.of("waterlogged")

        @JvmStatic protected val HOLDER_NORTH: VoxelShape = createCuboidShape(7.0, 7.0, 2.0, 9.0, 9.0, 6.0)
        @JvmStatic protected val HOLDER_EAST: VoxelShape = createCuboidShape(10.0, 7.0, 7.0, 14.0, 9.0, 9.0)
        @JvmStatic protected val HOLDER_SOUTH: VoxelShape = createCuboidShape(7.0, 7.0, 10.0, 9.0, 9.0, 14.0)
        @JvmStatic protected val HOLDER_WEST: VoxelShape = createCuboidShape(2.0, 7.0, 7.0, 6.0, 9.0, 9.0)
        @JvmStatic protected val HOLDER_UP: VoxelShape = createCuboidShape(7.0, 10.0, 7.0, 9.0, 14.0, 9.0)
        @JvmStatic protected val HOLDER_DOWN: VoxelShape = createCuboidShape(7.0, 2.0, 7.0, 9.0, 6.0, 9.0)

        @JvmStatic private val SHAPE_CORE: VoxelShape = createCuboidShape(6.0, 6.0, 6.0, 10.0, 10.0, 10.0)
        @JvmStatic private val SHAPE_NORTH: VoxelShape = createCuboidShape(6.0, 6.0, 0.0, 10.0, 10.0, 6.0)
        @JvmStatic private val SHAPE_EAST: VoxelShape = createCuboidShape(10.0, 6.0, 6.0, 16.0, 10.0, 10.0)
        @JvmStatic private val SHAPE_SOUTH: VoxelShape = createCuboidShape(6.0, 6.0, 10.0, 10.0, 10.0, 16.0)
        @JvmStatic private val SHAPE_WEST: VoxelShape = createCuboidShape(0.0, 6.0, 6.0, 6.0, 10.0, 10.0)
        @JvmStatic private val SHAPE_UP: VoxelShape = createCuboidShape(6.0, 10.0, 6.0, 10.0, 16.0, 10.0)
        @JvmStatic private val SHAPE_DOWN: VoxelShape =  createCuboidShape(6.0, 0.0, 6.0, 10.0, 6.0, 10.0)

        @JvmStatic
        protected fun getCableShape(state: BlockState): VoxelShape {
            var shape = SHAPE_CORE
            
            if (state.get(NORTH)) shape = VoxelShapes.union(shape, SHAPE_NORTH)
            if (state.get(EAST)) shape = VoxelShapes.union(shape, SHAPE_EAST)
            if (state.get(SOUTH)) shape = VoxelShapes.union(shape, SHAPE_SOUTH)
            if (state.get(WEST)) shape = VoxelShapes.union(shape, SHAPE_WEST)
            if (state.get(UP)) shape = VoxelShapes.union(shape, SHAPE_UP)
            if (state.get(DOWN)) shape = VoxelShapes.union(shape, SHAPE_DOWN)
            
            return shape
        }
    }
}