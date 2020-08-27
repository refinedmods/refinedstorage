package com.refinedmods.refinedstorage.block

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.block.shape.ShapeCache.getOrCreate
//import com.refinedmods.refinedstorage.tile.DestructorTile
import com.refinedmods.refinedstorage.tile.NoOpBlockEntity
import com.refinedmods.refinedstorage.util.BlockUtils
import com.thinkslynk.fabric.annotations.registry.RegisterBlock
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
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

@RegisterBlock(RS.ID, DestructorBlock.ID)
class DestructorBlock:
        CableBlock(BlockUtils.DEFAULT_GLASS_PROPERTIES, true)
//        BlockEntityProvider
{
    override val direction: BlockDirection
        get() = BlockDirection.ANY

//    override fun createBlockEntity(world: BlockView): BlockEntity {
//        return NoOpBlockEntity()
//        // TODO BlockEntities
////        return DestructorTile()
//    }

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        return getOrCreate(state, Function { s: BlockState ->
            var shape = getCableShape(s)
            shape = VoxelShapes.union(shape, getHeadShape(s))
            shape
        })
    }

    private fun getHeadShape(state: BlockState): VoxelShape {
        return HEAD_NORTH
//        val direction: Direction = state.get(direction.property)
//        return when {
//            direction === Direction.NORTH -> HEAD_NORTH
//            direction === Direction.EAST -> HEAD_EAST
//            direction === Direction.SOUTH -> HEAD_SOUTH
//            direction === Direction.WEST -> HEAD_WEST
//            direction === Direction.UP -> HEAD_UP
//            direction === Direction.DOWN -> HEAD_DOWN
//            else -> VoxelShapes.empty()
//        }
    }

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        // TODO Port Gui
//        return if (!world.isClient && CollisionUtils.isInBounds(getHeadShape(state), pos, hit.getHitVec())) {
//            NetworkUtils.attemptModify(world, pos, hit.getFace(), player) {
//                NetworkHooks.openGui(
//                        player as ServerPlayerEntity?,
//                        PositionalTileContainerProvider<DestructorTile>(
//                                TranslationTextComponent("gui.refinedstorage.destructor"),
//                                { tile: DestructorTile?, windowId: Int, inventory: PlayerInventory?, p: PlayerEntity? -> DestructorContainer(tile, player, windowId) },
//                                pos
//                        ),
//                        pos
//                )
//            }
//        } else ActionResult.SUCCESS

        return ActionResult.SUCCESS
    }

    companion object {
        const val ID = "destructor"
        private val HEAD_NORTH: VoxelShape = VoxelShapes.union(createCuboidShape(2.0, 2.0, 0.0, 14.0, 14.0, 2.0), HOLDER_NORTH)
        private val HEAD_EAST: VoxelShape = VoxelShapes.union(createCuboidShape(14.0, 2.0, 2.0, 16.0, 14.0, 14.0), HOLDER_EAST)
        private val HEAD_SOUTH: VoxelShape = VoxelShapes.union(createCuboidShape(2.0, 2.0, 14.0, 14.0, 14.0, 16.0), HOLDER_SOUTH)
        private val HEAD_WEST: VoxelShape = VoxelShapes.union(createCuboidShape(0.0, 2.0, 2.0, 2.0, 14.0, 14.0), HOLDER_WEST)
        private val HEAD_DOWN: VoxelShape = VoxelShapes.union(createCuboidShape(2.0, 0.0, 2.0, 14.0, 2.0, 14.0), HOLDER_DOWN)
        private val HEAD_UP: VoxelShape = VoxelShapes.union(createCuboidShape(2.0, 14.0, 2.0, 14.0, 16.0, 14.0), HOLDER_UP)
    }

}