package com.refinedmods.refinedstorage.block

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.container.NetworkTransmitterContainer
import com.refinedmods.refinedstorage.container.factory.PositionalTileContainerProvider
import com.refinedmods.refinedstorage.tile.NetworkTransmitterTile
import com.refinedmods.refinedstorage.util.BlockUtils
import com.refinedmods.refinedstorage.util.NetworkUtils
import com.thinkslynk.fabric.annotations.registry.RegisterBlock
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World

@RegisterBlock(RS.ID, NetworkTransmitterBlock.ID)
class NetworkTransmitterBlock:
        NetworkNodeBlock(BlockUtils.DEFAULT_ROCK_PROPERTIES, true),
        BlockEntityProvider
{
    companion object {
        const val ID = "network_transmitter"
    }

    override fun createBlockEntity(world: BlockView): BlockEntity? = NetworkTransmitterTile()

    override fun onUse(state: BlockState?, world: World?, pos: BlockPos?, player: PlayerEntity?, hand: Hand?, hit: BlockHitResult?): ActionResult {
        // TODO Port Gui
//        return if (!world.isClient) {
//            NetworkUtils.attemptModify(world, pos, hit.getFace(), player) {
//                NetworkHooks.openGui(
//                        player as ServerPlayerEntity?,
//                        PositionalTileContainerProvider<NetworkTransmitterTile>(
//                                TranslationTextComponent("gui.refinedstorage.network_transmitter"),
//                                { tile: NetworkTransmitterTile?, windowId: Int, inventory: PlayerInventory?, p: PlayerEntity? -> NetworkTransmitterContainer(tile, player, windowId) },
//                                pos
//                        ),
//                        pos
//                )
//            }
//        } else ActionResult.SUCCESS
        return ActionResult.SUCCESS
    }
}