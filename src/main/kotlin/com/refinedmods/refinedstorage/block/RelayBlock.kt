package com.refinedmods.refinedstorage.block

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.util.BlockUtils
import com.thinkslynk.fabric.annotations.registry.RegisterBlock
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

@RegisterBlock(RS.ID, RelayBlock.ID)
class RelayBlock:
        NetworkNodeBlock(BlockUtils.DEFAULT_ROCK_PROPERTIES, true)
//        BlockEntityProvider
{
    companion object {
        const val ID = "relay"
    }

//    override fun createBlockEntity(world: BlockView?): BlockEntity?
//            = NoOpBlockEntity()
//    // TODO BlockEntities
////            = RelayTile()

    override fun onUse(state: BlockState?, world: World?, pos: BlockPos?, player: PlayerEntity?, hand: Hand?, hit: BlockHitResult?): ActionResult {
        // TODO Port Gui
//        return if (!world.isClient) {
//            NetworkUtils.attemptModify(world, pos, hit.getFace(), player) {
//                NetworkHooks.openGui(
//                        player as ServerPlayerEntity?,
//                        PositionalTileContainerProvider<RelayTile>(
//                                TranslationTextComponent("gui.refinedstorage.relay"),
//                                { tile: RelayTile?, windowId: Int, inventory: PlayerInventory?, p: PlayerEntity? -> RelayContainer(tile, player, windowId) },
//                                pos
//                        ),
//                        pos
//                )
//            }
//        } else ActionResult.SUCCESS
        return ActionResult.SUCCESS
    }
}