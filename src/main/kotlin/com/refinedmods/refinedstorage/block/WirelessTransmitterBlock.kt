package com.refinedmods.refinedstorage.block

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.util.BlockUtils
import com.thinkslynk.fabric.annotations.registry.RegisterBlock
import com.thinkslynk.fabric.annotations.registry.RegisterBlockItem
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.ShapeContext
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.state.StateManager
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World

@RegisterBlock(RS.ID, WirelessTransmitterBlock.ID)
@RegisterBlockItem(RS.ID, WirelessTransmitterBlock.ID, "MISC")
class WirelessTransmitterBlock:
        NetworkNodeBlock(BlockUtils.DEFAULT_ROCK_PROPERTIES)
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

//    override fun createBlockEntity(world: BlockView?): BlockEntity?
//            = NoOpBlockEntity()
    // TODO BlockEntities
//            = WirelessTransmitterTile()

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape {
        return SHAPE_NORTH
//        return when (state.get(direction.property)) {
//            Direction.DOWN -> SHAPE_DOWN
//            Direction.UP -> SHAPE_UP
//            Direction.NORTH -> SHAPE_NORTH
//            Direction.SOUTH -> SHAPE_SOUTH
//            Direction.WEST -> SHAPE_WEST
//            Direction.EAST -> SHAPE_EAST
//            else -> VoxelShapes.empty()
//        }
    }

    override fun onUse(state: BlockState?, world: World?, pos: BlockPos?, player: PlayerEntity?, hand: Hand?, hit: BlockHitResult?): ActionResult {
        // TODO Port Gui
//        return if (!world.isClient) {
//            NetworkUtils.attemptModify(world, pos, hit.getFace(), player) {
//                NetworkHooks.openGui(
//                        player as ServerPlayerEntity?,
//                        PositionalTileContainerProvider<WirelessTransmitterTile>(
//                                TranslationTextComponent("gui.refinedstorage.wireless_transmitter"),
//                                { tile: WirelessTransmitterTile?, windowId: Int, inventory: PlayerInventory?, p: PlayerEntity? -> WirelessTransmitterContainer(tile, player, windowId) },
//                                pos
//                        ),
//                        pos
//                )
//            }
//        } else ActionResult.SUCCESS
        return ActionResult.SUCCESS
    }

    companion object {
        const val ID = "wireless_transmitter"
        private val SHAPE_DOWN: VoxelShape = createCuboidShape(6.0, 0.0, 6.0, 10.0, 10.0, 10.0)
        private val SHAPE_UP: VoxelShape = createCuboidShape(6.0, 6.0, 6.0, 10.0, 16.0, 10.0)
        private val SHAPE_EAST: VoxelShape = createCuboidShape(6.0, 6.0, 6.0, 16.0, 10.0, 10.0)
        private val SHAPE_WEST: VoxelShape = createCuboidShape(0.0, 6.0, 6.0, 10.0, 10.0, 10.0)
        private val SHAPE_NORTH: VoxelShape = createCuboidShape(6.0, 6.0, 0.0, 10.0, 10.0, 10.0)
        private val SHAPE_SOUTH: VoxelShape = createCuboidShape(6.0, 6.0, 6.0, 10.0, 10.0, 16.0)
    }

}