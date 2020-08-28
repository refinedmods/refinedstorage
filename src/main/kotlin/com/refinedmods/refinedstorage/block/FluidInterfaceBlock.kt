package com.refinedmods.refinedstorage.block

import com.refinedmods.refinedstorage.RS
//import com.refinedmods.refinedstorage.api.network.security.Permission
//import com.refinedmods.refinedstorage.container.FluidInterfaceContainer
//import com.refinedmods.refinedstorage.container.factory.PositionalTileContainerProvider
//import com.refinedmods.refinedstorage.tile.FluidInterfaceTile
import com.refinedmods.refinedstorage.util.BlockUtils
//import com.refinedmods.refinedstorage.util.NetworkUtils
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

@RegisterBlock(RS.ID, FluidInterfaceBlock.ID)
@RegisterBlockItem(RS.ID, FluidInterfaceBlock.ID, "CURED_STORAGE")
class FluidInterfaceBlock:
        NetworkNodeBlock(BlockUtils.DEFAULT_ROCK_PROPERTIES)
//        BlockEntityProvider
{
    companion object{
        const val ID = "fluid_interface"
    }

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