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
import net.minecraft.state.property.BooleanProperty
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World

@RegisterBlock(RS.ID, DetectorBlock.ID)
@RegisterBlockItem(RS.ID, DetectorBlock.ID, "CURED_STORAGE")
class DetectorBlock:
        NetworkNodeBlock(BlockUtils.DEFAULT_ROCK_PROPERTIES) // TODO Double check connected value
//        BlockEntityProvider
{

//    override fun createBlockEntity(world: BlockView?): BlockEntity? {
//        return NoOpBlockEntity()
//        // TODO BlockEntities
////        return DetectorTile()
//    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(POWERED)
    }

    override fun getOutlineShape(state: BlockState, world: BlockView, pos: BlockPos, context: ShapeContext): VoxelShape
            = SHAPE

// TODO Power
//    fun canProvidePower(state: BlockState?): Boolean {
//        return true
//    }
//
//    fun getWeakPower(state: BlockState?, world: BlockView, pos: BlockPos?, side: Direction?): Int {
//        val tile: BlockEntity = world.getBlockEntity(pos)!!
//        return if (tile is DetectorTile && tile.node!!.isPowered) 15 else 0
//    }

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        // TODO Port Gui
//        return if (!world.isClient) {
//            NetworkUtils.attemptModify(world, pos, hit.getFace(), player) {
//                NetworkHooks.openGui(
//                        player as ServerPlayerEntity?,
//                        PositionalTileContainerProvider<DetectorTile>(
//                                TranslationTextComponent("gui.refinedstorage.detector"),
//                                { tile: DetectorTile?, windowId: Int, inventory: PlayerInventory?, p: PlayerEntity? -> DetectorContainer(tile, player, windowId) },
//                                pos
//                        ),
//                        pos
//                )
//            }
//        } else ActionResult.SUCCESS

        return ActionResult.SUCCESS
    }

    companion object {
        const val ID = "detector"
        val POWERED: BooleanProperty = BooleanProperty.of("powered")
        private val SHAPE: VoxelShape = createCuboidShape(0.0, 0.0, 0.0, 16.0, 5.0, 16.0)
    }

    init {
        defaultState = stateManager.defaultState
                .with(POWERED, false)
    }
}