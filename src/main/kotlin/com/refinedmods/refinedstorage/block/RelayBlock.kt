package com.refinedmods.refinedstorage.block

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.tile.NoOpBlockEntity
import com.refinedmods.refinedstorage.util.BlockUtils
import com.thinkslynk.fabric.annotations.registry.RegisterBlock
import com.thinkslynk.fabric.annotations.registry.RegisterBlockItem
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World

@RegisterBlock(RS.ID, RelayBlock.ID)
@RegisterBlockItem(RS.ID, RelayBlock.ID, "R_S_ITEM_GROUP")
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