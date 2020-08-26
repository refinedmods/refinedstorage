package com.refinedmods.refinedstorage.block

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.storage.cache.InvalidateCause
import com.refinedmods.refinedstorage.apiimpl.network.node.ExternalStorageNetworkNode
import com.refinedmods.refinedstorage.block.shape.ShapeCache.getOrCreate
import com.refinedmods.refinedstorage.container.ExternalStorageContainer
import com.refinedmods.refinedstorage.container.factory.PositionalTileContainerProvider
import com.refinedmods.refinedstorage.tile.ExternalStorageTile
import com.refinedmods.refinedstorage.util.BlockUtils
import com.refinedmods.refinedstorage.util.CollisionUtils
import com.refinedmods.refinedstorage.util.NetworkUtils
import com.thinkslynk.fabric.annotations.registry.RegisterBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.server.network.ServerPlayerEntity
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

@RegisterBlock(RS.ID, ExternalStorageBlock.ID)
class ExternalStorageBlock:
        CableBlock(BlockUtils.DEFAULT_GLASS_PROPERTIES, false), // TODO Check connected
        BlockEntityProvider
{
    override val direction: BlockDirection
        get() = BlockDirection.ANY

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        return getOrCreate(state, Function { s: BlockState ->
            VoxelShapes.union(getCableShape(s), getHeadShape(s))
        })
    }

    private fun getHeadShape(state: BlockState): VoxelShape {
        return when(state.get(direction.property)){
            Direction.NORTH -> HEAD_NORTH
            Direction.EAST -> HEAD_EAST
            Direction.SOUTH -> HEAD_SOUTH
            Direction.WEST -> HEAD_WEST
            Direction.UP -> HEAD_UP
            Direction.DOWN -> HEAD_DOWN
            else -> VoxelShapes.empty()
        }
    }

    override fun createBlockEntity(world: BlockView): BlockEntity = ExternalStorageTile()

    override fun onUse(state: BlockState?, world: World?, pos: BlockPos?, player: PlayerEntity?, hand: Hand?, hit: BlockHitResult?): ActionResult {
        // TODO Port Gui
//        return if (!world.isClient && CollisionUtils.isInBounds(getHeadShape(state), pos, hit.getHitVec())) {
//            NetworkUtils.attemptModify(world, pos, hit.getFace(), player) {
//                NetworkHooks.openGui(
//                        player as ServerPlayerEntity?,
//                        PositionalTileContainerProvider<ExternalStorageTile>(
//                                TranslationTextComponent("gui.refinedstorage.external_storage"),
//                                { tile: ExternalStorageTile?, windowId: Int, inventory: PlayerInventory?, p: PlayerEntity? -> ExternalStorageContainer(tile, player, windowId) },
//                                pos
//                        ),
//                        pos
//                )
//            }
//        } else ActionResult.SUCCESS

        return ActionResult.SUCCESS
    }

    override fun neighborUpdate(state: BlockState, world: World, pos: BlockPos, block: Block, fromPos: BlockPos, notify: Boolean) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify)

        if (!world.isClient) {
            val node = NetworkUtils.getNodeFromTile(world.getBlockEntity(pos))
            if (node is ExternalStorageNetworkNode && node.network != null && fromPos == pos.offset(node.direction)) {
                node.updateStorage(node.network, InvalidateCause.NEIGHBOR_CHANGED)
            }
        }
    }

    companion object {
        const val ID = "external_storage"
        private val HEAD_NORTH: VoxelShape = VoxelShapes.union(createCuboidShape(3.0, 3.0, 0.0, 13.0, 13.0, 2.0), HOLDER_NORTH)
        private val HEAD_EAST: VoxelShape = VoxelShapes.union(createCuboidShape(14.0, 3.0, 3.0, 16.0, 13.0, 13.0), HOLDER_EAST)
        private val HEAD_SOUTH: VoxelShape = VoxelShapes.union(createCuboidShape(3.0, 3.0, 14.0, 13.0, 13.0, 16.0), HOLDER_SOUTH)
        private val HEAD_WEST: VoxelShape = VoxelShapes.union(createCuboidShape(0.0, 3.0, 3.0, 2.0, 13.0, 13.0), HOLDER_WEST)
        private val HEAD_UP: VoxelShape = VoxelShapes.union(createCuboidShape(3.0, 14.0, 3.0, 13.0, 16.0, 13.0), HOLDER_UP)
        private val HEAD_DOWN: VoxelShape = VoxelShapes.union(createCuboidShape(3.0, 0.0, 3.0, 13.0, 2.0, 13.0), HOLDER_DOWN)
    }
}