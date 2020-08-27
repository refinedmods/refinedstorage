package com.refinedmods.refinedstorage.block

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.block.shape.ShapeCache.getOrCreate
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

@RegisterBlock(RS.ID, ExporterBlock.ID)
class ExporterBlock:
        CableBlock(BlockUtils.DEFAULT_GLASS_PROPERTIES, false) // TODO Check connected
//        BlockEntityProvider
{
    override val direction: BlockDirection
        get() = BlockDirection.ANY

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        return getOrCreate(state, Function { s: BlockState ->
            VoxelShapes.union(getCableShape(s), getLineShape(s))
        })
    }

    private fun getLineShape(state: BlockState): VoxelShape {
        return LINE_NORTH
//        val direction: Direction = state.get(direction.property)
//        return when {
//            direction === Direction.NORTH -> LINE_NORTH
//            direction === Direction.EAST -> LINE_EAST
//            direction === Direction.SOUTH -> LINE_SOUTH
//            direction === Direction.WEST -> LINE_WEST
//            direction === Direction.UP -> LINE_UP
//            direction === Direction.DOWN -> LINE_DOWN
//            else -> VoxelShapes.empty()
//        }
    }

//    override fun createBlockEntity(world: BlockView): BlockEntity
//            = NoOpBlockEntity()
//    // TODO BlockEntities
////            = ExporterTile()

    override fun onUse(state: BlockState?, world: World?, pos: BlockPos?, player: PlayerEntity?, hand: Hand?, hit: BlockHitResult?): ActionResult {
        // TODO Port Gui
//        return if (!world.isClient && CollisionUtils.isInBounds(getLineShape(state), pos, hit.getHitVec())) {
//            NetworkUtils.attemptModify(world, pos, hit.getFace(), player) {
//                NetworkHooks.openGui(
//                        player as ServerPlayerEntity?,
//                        PositionalTileContainerProvider<ExporterTile>(
//                                TranslationTextComponent("gui.refinedstorage.exporter"),
//                                { tile: ExporterTile?, windowId: Int, inventory: PlayerInventory?, p: PlayerEntity? -> ExporterContainer(tile, player, windowId) },
//                                pos
//                        ),
//                        pos
//                )
//            }
//        } else ActionResult.SUCCESS
        return ActionResult.SUCCESS
    }

    companion object {
        const val ID = "exporter"
        private val LINE_NORTH_1: VoxelShape = createCuboidShape(6.0, 6.0, 0.0, 10.0, 10.0, 2.0)
        private val LINE_NORTH_2: VoxelShape = createCuboidShape(5.0, 5.0, 2.0, 11.0, 11.0, 4.0)
        private val LINE_NORTH_3: VoxelShape = createCuboidShape(3.0, 3.0, 4.0, 13.0, 13.0, 6.0)
        private val LINE_NORTH: VoxelShape = VoxelShapes.union(LINE_NORTH_1, LINE_NORTH_2, LINE_NORTH_3)
        private val LINE_EAST_1: VoxelShape = createCuboidShape(14.0, 6.0, 6.0, 16.0, 10.0, 10.0)
        private val LINE_EAST_2: VoxelShape = createCuboidShape(12.0, 5.0, 5.0, 14.0, 11.0, 11.0)
        private val LINE_EAST_3: VoxelShape = createCuboidShape(10.0, 3.0, 3.0, 12.0, 13.0, 13.0)
        private val LINE_EAST: VoxelShape = VoxelShapes.union(LINE_EAST_1, LINE_EAST_2, LINE_EAST_3)
        private val LINE_SOUTH_1: VoxelShape = createCuboidShape(6.0, 6.0, 14.0, 10.0, 10.0, 16.0)
        private val LINE_SOUTH_2: VoxelShape = createCuboidShape(5.0, 5.0, 12.0, 11.0, 11.0, 14.0)
        private val LINE_SOUTH_3: VoxelShape = createCuboidShape(3.0, 3.0, 10.0, 13.0, 13.0, 12.0)
        private val LINE_SOUTH: VoxelShape = VoxelShapes.union(LINE_SOUTH_1, LINE_SOUTH_2, LINE_SOUTH_3)
        private val LINE_WEST_1: VoxelShape = createCuboidShape(0.0, 6.0, 6.0, 2.0, 10.0, 10.0)
        private val LINE_WEST_2: VoxelShape = createCuboidShape(2.0, 5.0, 5.0, 4.0, 11.0, 11.0)
        private val LINE_WEST_3: VoxelShape = createCuboidShape(4.0, 3.0, 3.0, 6.0, 13.0, 13.0)
        private val LINE_WEST: VoxelShape = VoxelShapes.union(LINE_WEST_1, LINE_WEST_2, LINE_WEST_3)
        private val LINE_UP_1: VoxelShape = createCuboidShape(6.0, 14.0, 6.0, 10.0, 16.0, 10.0)
        private val LINE_UP_2: VoxelShape = createCuboidShape(5.0, 12.0, 5.0, 11.0, 14.0, 11.0)
        private val LINE_UP_3: VoxelShape = createCuboidShape(3.0, 10.0, 3.0, 13.0, 12.0, 13.0)
        private val LINE_UP: VoxelShape = VoxelShapes.union(LINE_UP_1, LINE_UP_2, LINE_UP_3)
        private val LINE_DOWN_1: VoxelShape = createCuboidShape(6.0, 0.0, 6.0, 10.0, 2.0, 10.0)
        private val LINE_DOWN_2: VoxelShape = createCuboidShape(5.0, 2.0, 5.0, 11.0, 4.0, 11.0)
        private val LINE_DOWN_3: VoxelShape = createCuboidShape(3.0, 4.0, 3.0, 13.0, 6.0, 13.0)
        private val LINE_DOWN: VoxelShape = VoxelShapes.union(LINE_DOWN_1, LINE_DOWN_2, LINE_DOWN_3)
    }
}