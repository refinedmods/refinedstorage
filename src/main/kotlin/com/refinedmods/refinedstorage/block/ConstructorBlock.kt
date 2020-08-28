package com.refinedmods.refinedstorage.block

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.block.shape.ShapeCache.getOrCreate
import com.refinedmods.refinedstorage.extensions.getCustomLogger
//import com.refinedmods.refinedstorage.tile.ConstructorTile
import com.refinedmods.refinedstorage.util.BlockUtils
import com.thinkslynk.fabric.annotations.registry.RegisterBlock
import com.thinkslynk.fabric.annotations.registry.RegisterBlockItem
import com.thinkslynk.fabric.helpers.AnnotationHelpers
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import java.util.function.Function

@RegisterBlock(RS.ID, ConstructorBlock.ID)
@RegisterBlockItem(RS.ID, ConstructorBlock.ID, "MISC")
class ConstructorBlock:
        CableBlock(BlockUtils.DEFAULT_GLASS_PROPERTIES)
//        BlockEntityProvider
{
    override val direction: BlockDirection
        get() = BlockDirection.ANY

    init {
        defaultState = defaultState.with(CONNECTED, false)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(CONNECTED)
    }

    // TODO BlockEntities
//    override fun createBlockEntity(world: BlockView): {
//        BlockEntity = ConstructorTile()
//    }

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        return getOrCreate(state, Function { s: BlockState ->
            VoxelShapes.union(getCableShape(s), getHeadShape(s))
        })
    }

    private fun getHeadShape(state: BlockState): VoxelShape {
        return when(state.get(this.direction.property)) {
            Direction.DOWN -> HEAD_DOWN
            Direction.UP -> HEAD_UP
            Direction.NORTH -> HEAD_NORTH
            Direction.SOUTH -> HEAD_SOUTH
            Direction.WEST -> HEAD_WEST
            Direction.EAST -> HEAD_EAST
            else -> VoxelShapes.empty()
        }
    }

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        // TODO Port Gui
//        return if (!world.isClient && CollisionUtils.isInBounds(getHeadShape(state), pos, hit.getHitVec())) {
//            NetworkUtils.attemptModify(world, pos, hit.getFace(), player) {
//                NetworkHooks.openGui(
//                        player as ServerPlayerEntity?,
//                        PositionalTileContainerProvider<ConstructorTile>(
//                                TranslationTextComponent("gui.refinedstorage.constructor"),
//                                { tile: ConstructorTile?, windowId: Int, inventory: PlayerInventory?, p: PlayerEntity? -> ConstructorContainer(tile, player, windowId) },
//                                pos
//                        ),
//                        pos
//                )
//            }
//        } else ActionResult.SUCCESS
        return ActionResult.SUCCESS
    }

    companion object {
        val log = getCustomLogger(CableBlock::class)

        const val ID = "constructor"

        private val HEAD_NORTH: VoxelShape = VoxelShapes.union(createCuboidShape(2.0, 2.0, 0.0, 14.0, 14.0, 2.0), HOLDER_NORTH)
        private val HEAD_EAST: VoxelShape = VoxelShapes.union(createCuboidShape(14.0, 2.0, 2.0, 16.0, 14.0, 14.0), HOLDER_EAST)
        private val HEAD_SOUTH: VoxelShape = VoxelShapes.union(createCuboidShape(2.0, 2.0, 14.0, 14.0, 14.0, 16.0), HOLDER_SOUTH)
        private val HEAD_WEST: VoxelShape = VoxelShapes.union(createCuboidShape(0.0, 2.0, 2.0, 2.0, 14.0, 14.0), HOLDER_WEST)
        private val HEAD_DOWN: VoxelShape = VoxelShapes.union(createCuboidShape(2.0, 0.0, 2.0, 14.0, 2.0, 14.0), HOLDER_DOWN)
        private val HEAD_UP: VoxelShape = VoxelShapes.union(createCuboidShape(2.0, 14.0, 2.0, 14.0, 16.0, 14.0), HOLDER_UP)
    }
}