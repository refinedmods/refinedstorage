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

@RegisterBlock(RS.ID, InterfaceBlock.ID)
@RegisterBlockItem(RS.ID, InterfaceBlock.ID, "R_S_ITEM_GROUP")
class InterfaceBlock:
        NetworkNodeBlock(BlockUtils.DEFAULT_ROCK_PROPERTIES)
//        BlockEntityProvider
{
    companion object {
        const val ID = "interface"
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
//            = InterfaceTile()

    override fun onUse(state: BlockState?, world: World?, pos: BlockPos?, player: PlayerEntity?, hand: Hand?, hit: BlockHitResult?): ActionResult {
        // TODO Port Gui
//        return if (!world.isClient) {
//            NetworkUtils.attempt(world, pos, hit.getFace(), player, Runnable {
//                NetworkHooks.openGui(
//                        player as ServerPlayerEntity?,
//                        PositionalTileContainerProvider<InterfaceTile>(
//                                TranslationTextComponent("gui.refinedstorage.interface"),
//                                { tile: InterfaceTile?, windowId: Int, inventory: PlayerInventory?, p: PlayerEntity? -> InterfaceContainer(tile, player, windowId) },
//                                pos
//                        ),
//                        pos
//                )
//            }, Permission.MODIFY, Permission.INSERT, Permission.EXTRACT)
//        } else ActionResult.SUCCESS

        return ActionResult.SUCCESS
    }
}