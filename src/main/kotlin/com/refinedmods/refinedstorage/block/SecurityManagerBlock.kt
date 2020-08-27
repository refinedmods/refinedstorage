package com.refinedmods.refinedstorage.block

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.tile.NoOpBlockEntity
import com.refinedmods.refinedstorage.util.BlockUtils
import com.thinkslynk.fabric.annotations.registry.RegisterBlock
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

@RegisterBlock(RS.ID, SecurityManagerBlock.ID)
class SecurityManagerBlock:
        NetworkNodeBlock(BlockUtils.DEFAULT_ROCK_PROPERTIES, true)
//        BlockEntityProvider
{
    companion object {
        const val ID = "security_manager"
    }

//    override fun createBlockEntity(world: BlockView?): BlockEntity?
//            = NoOpBlockEntity()
    // TODO BlockEntities
//            = SecurityManagerTile()

    override val direction: BlockDirection
        get() = BlockDirection.HORIZONTAL

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        // TODO Port Gui
//        if (!world.isClient) {
//            val action = Runnable {
//                NetworkHooks.openGui(
//                        player as ServerPlayerEntity,
//                        PositionalTileContainerProvider<SecurityManagerTile>(
//                                TranslationTextComponent("gui.refinedstorage.security_manager"),
//                                { tile: SecurityManagerTile?, windowId: Int, inventory: PlayerInventory?, p: PlayerEntity? -> SecurityManagerContainer(tile, player, windowId) },
//                                pos
//                        ),
//                        pos
//                )
//            }
//            if (player.gameProfile.id == (world.getBlockEntity(pos) as SecurityManagerTile?)!!.node.owner) {
//                action.run()
//            } else {
//                return NetworkUtils.attempt(world, pos, hit.getFace(), player, action, Permission.MODIFY, Permission.SECURITY)
//            }
//        }
        return ActionResult.SUCCESS
    }
}