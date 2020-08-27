package com.refinedmods.refinedstorage.block

import com.refinedmods.refinedstorage.RS
//import com.refinedmods.refinedstorage.api.network.security.Permission
//import com.refinedmods.refinedstorage.container.FluidInterfaceContainer
//import com.refinedmods.refinedstorage.container.factory.PositionalTileContainerProvider
//import com.refinedmods.refinedstorage.tile.FluidInterfaceTile
import com.refinedmods.refinedstorage.tile.NoOpBlockEntity
import com.refinedmods.refinedstorage.util.BlockUtils
//import com.refinedmods.refinedstorage.util.NetworkUtils
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

@RegisterBlock(RS.ID, FluidInterfaceBlock.ID)
class FluidInterfaceBlock:
        NetworkNodeBlock(BlockUtils.DEFAULT_ROCK_PROPERTIES, true)
//        BlockEntityProvider
{
    companion object{
        const val ID = "fluid_interface"
    }

//    override fun createBlockEntity(world: BlockView?): BlockEntity?
//            = NoOpBlockEntity()
    // TODO BlockEntities
//            = FluidInterfaceTile()

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        // TODO Port Gui
//        return if (!world.isClient) {
//            NetworkUtils.attempt(world, pos, hit.getFace(), player, Runnable {
//                NetworkHooks.openGui(
//                        player as ServerPlayerEntity?,
//                        PositionalTileContainerProvider<FluidInterfaceTile>(
//                                TranslationTextComponent("gui.refinedstorage.fluid_interface"),
//                                { tile: FluidInterfaceTile?, windowId: Int, inventory: PlayerInventory?, p: PlayerEntity? -> FluidInterfaceContainer(tile, player, windowId) },
//                                pos
//                        ),
//                        pos
//                )
//            }, Permission.MODIFY, Permission.INSERT, Permission.EXTRACT)
//        } else ActionResult.SUCCESS
        return ActionResult.SUCCESS
    }
}