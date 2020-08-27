package com.refinedmods.refinedstorage.block

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.util.BlockUtils
import com.thinkslynk.fabric.annotations.registry.RegisterBlock
import com.thinkslynk.fabric.annotations.registry.RegisterBlockItem
import net.minecraft.block.Block
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.state.StateManager
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

@RegisterBlock(RS.ID, DiskManipulatorBlock.ID)
@RegisterBlockItem(RS.ID, DiskManipulatorBlock.ID, "R_S_ITEM_GROUP")
class DiskManipulatorBlock:
        NetworkNodeBlock(BlockUtils.DEFAULT_ROCK_PROPERTIES)
//        BlockEntityProvider
{
    companion object {
        const val ID = "disk_manipulator"
    }

    init {
        defaultState = defaultState.with(CONNECTED, false)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(CONNECTED)
    }

//    override fun createBlockEntity(world: BlockView): BlockEntity?
//            = NoOpBlockEntity()
//    // TODO BlockEntities
////            = DiskManipulatorTile()

    override fun onUse(state: BlockState?, world: World?, pos: BlockPos?, player: PlayerEntity?, hand: Hand?, hit: BlockHitResult?): ActionResult {
        // TODO Port Gui
//        return if (!world.isClient) {
//            NetworkUtils.attemptModify(world, pos, rayTraceResult.getFace(), player) {
//                NetworkHooks.openGui(
//                        player as ServerPlayerEntity?,
//                        PositionalTileContainerProvider<DiskManipulatorTile>(
//                                TranslationTextComponent("gui.refinedstorage.disk_manipulator"),
//                                { tile: DiskManipulatorTile?, windowId: Int, inventory: PlayerInventory?, p: PlayerEntity? -> DiskManipulatorContainer(tile, p, windowId) },
//                                pos
//                        ),
//                        pos
//                )
//            }
//        } else ActionResult.SUCCESS
        return ActionResult.SUCCESS
    }

    override val direction: BlockDirection
        get() = BlockDirection.HORIZONTAL

}