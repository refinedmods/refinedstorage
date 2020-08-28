package com.refinedmods.refinedstorage.block

import com.refinedmods.refinedstorage.RS
//import com.refinedmods.refinedstorage.tile.craftingmonitor.CraftingMonitorTile
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

@RegisterBlock(RS.ID, CraftingMonitorBlock.ID)
@RegisterBlockItem(RS.ID, CraftingMonitorBlock.ID, "MISC")
class CraftingMonitorBlock:
        NetworkNodeBlock(BlockUtils.DEFAULT_ROCK_PROPERTIES)
//        BlockEntityProvider
{
    companion object {
        const val ID = "crafting_monitor"
    }
    override val direction: BlockDirection
        get() = BlockDirection.HORIZONTAL

    init {
        defaultState = defaultState.with(CONNECTED, false)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(CONNECTED)
    }

//    override fun createBlockEntity(world: BlockView?): BlockEntity? {
//        return NoOpBlockEntity()
//        // TODO BlockEntities
////        return CraftingMonitorTile()
//    }

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        // TODO Port Gui
//        if (!world.isClient) {
//            val tile = world.getBlockEntity(pos) as CraftingMonitorTile?
//            return NetworkUtils.attempt(world, pos, hit.getFace(), player, Runnable {
//                NetworkHooks.openGui(
//                        player as ServerPlayerEntity?,
//                        CraftingMonitorContainerProvider(RSContainers.CRAFTING_MONITOR, tile!!.node, tile),
//                        pos
//                )
//            }, Permission.MODIFY, Permission.AUTOCRAFTING)
//        }
        return ActionResult.SUCCESS
    }
}