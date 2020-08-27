package com.refinedmods.refinedstorage.block

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.block.shape.ShapeCache.getOrCreate
import com.refinedmods.refinedstorage.tile.CableTile
//import com.refinedmods.refinedstorage.capability.NetworkNodeProxyCapability
//import com.refinedmods.refinedstorage.tile.CableTile
import com.refinedmods.refinedstorage.util.BlockUtils
import com.thinkslynk.fabric.annotations.registry.RegisterBlock
import com.thinkslynk.fabric.annotations.registry.RegisterBlockItem
import com.thinkslynk.fabric.helpers.AnnotationHelpers
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.util.function.BooleanBiFunction
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import java.util.function.Function

@RegisterBlock(RS.ID, CableBlock.ID)
@RegisterBlockItem(RS.ID, CableBlock.ID, "R_S_ITEM_GROUP")
open class CableBlock(
        settingsIn: Settings = BlockUtils.DEFAULT_GLASS_PROPERTIES,
        connected: Boolean = false // TODO Check connected
): NetworkNodeBlock(settingsIn, connected),
        BlockEntityProvider
{
    init {
        val d  = stateManager.defaultState
                .with(NORTH, false)
                .with(EAST, false)
                .with(SOUTH, false)
                .with(WEST, false)
                .with(UP, false)
                .with(DOWN, false)
                .with(WATERLOGGED, false)

        defaultState = d
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

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        return getState(defaultState, ctx.world, ctx.blockPos)
    }

//    override fun getFluidState(state: BlockState): FluidState {
//        return when {
//            state.get(WATERLOGGED) -> Fluids.WATER.getStill(false)
//            else -> super.getFluidState(state)
//        }
//    }

    private fun hasNode(world: BlockView, pos: BlockPos, state: BlockState, direction: Direction): Boolean {
        // Prevent the "holder" of a cable block conflicting with a cable connection.
        // TODO Figure out which property this is supposed to be
//        if (state.get<Direction>(direction.property).opposite == direction) {
//            return false
//        }

        // TODO Replace capabilities
        return false

//        return world.getBlockEntity(pos)?.getCapability(NetworkNodeProxyCapability.NETWORK_NODE_PROXY_CAPABILITY, direction)?.isPresent()
//                ?: false
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
        @JvmStatic  private val SHAPE_CORE: VoxelShape = createCuboidShape(6.0, 6.0, 6.0, 10.0, 10.0, 10.0)
        @JvmStatic private val SHAPE_NORTH: VoxelShape = createCuboidShape(6.0, 6.0, 0.0, 10.0, 10.0, 6.0)
        @JvmStatic private val SHAPE_EAST: VoxelShape = createCuboidShape(10.0, 6.0, 6.0, 16.0, 10.0, 10.0)
        @JvmStatic private val SHAPE_SOUTH: VoxelShape = createCuboidShape(6.0, 6.0, 10.0, 10.0, 10.0, 16.0)
        @JvmStatic private val SHAPE_WEST: VoxelShape = createCuboidShape(0.0, 6.0, 6.0, 6.0, 10.0, 10.0)
        @JvmStatic private val SHAPE_UP: VoxelShape = createCuboidShape(6.0, 10.0, 6.0, 10.0, 16.0, 10.0)
        @JvmStatic private val SHAPE_DOWN: VoxelShape =  createCuboidShape(6.0, 0.0, 6.0, 10.0, 6.0, 10.0)

        @JvmStatic
        protected fun getCableShape(state: BlockState): VoxelShape {
            var shape = SHAPE_CORE
            
            if (state.get(NORTH)) shape = VoxelShapes.combine(shape, SHAPE_NORTH, BooleanBiFunction.OR)
            if (state.get(EAST)) shape = VoxelShapes.combine(shape, SHAPE_EAST, BooleanBiFunction.OR)
            if (state.get(SOUTH)) shape = VoxelShapes.combine(shape, SHAPE_SOUTH,BooleanBiFunction.OR)
            if (state.get(WEST)) shape = VoxelShapes.combine(shape, SHAPE_WEST,BooleanBiFunction.OR)
            if (state.get(UP)) shape = VoxelShapes.combine(shape, SHAPE_UP,BooleanBiFunction.OR)
            if (state.get(DOWN)) shape = VoxelShapes.combine(shape, SHAPE_DOWN,BooleanBiFunction.OR)
            
            return shape
        }
    }
}