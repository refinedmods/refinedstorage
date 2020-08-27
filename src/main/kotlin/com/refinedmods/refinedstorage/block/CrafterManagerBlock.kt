package com.refinedmods.refinedstorage.block

import com.refinedmods.refinedstorage.RS
//import com.refinedmods.refinedstorage.tile.CrafterManagerTile
import com.refinedmods.refinedstorage.util.BlockUtils
import com.thinkslynk.fabric.annotations.registry.RegisterBlock
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

@RegisterBlock(RS.ID, CrafterManagerBlock.ID)
class CrafterManagerBlock:
        NetworkNodeBlock(BlockUtils.DEFAULT_ROCK_PROPERTIES, true)
//        BlockEntityProvider
{
    companion object{
        const val ID = "crafter_manager"
    }
    override val direction: BlockDirection
        get() = BlockDirection.HORIZONTAL

//    override fun createBlockEntity(world: BlockView): BlockEntity? {
//        return NoOpBlockEntity()
//        // TODO BlockEntities
////        return CrafterManagerTile()
//    }

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        // TODO Port GUI
//        return if (!world.isClient) {
//            NetworkUtils.attempt(world, pos, hit.getFace(), player, Runnable {
//                NetworkHooks.openGui(
//                        player as ServerPlayerEntity?,
//                        CrafterManagerContainerProvider(world.getBlockEntity(pos) as CrafterManagerTile?),
//                        { buf -> CrafterManagerContainerProvider.writeToBuffer(buf, world, pos) }
//                )
//            }, Permission.MODIFY, Permission.AUTOCRAFTING)
//        } else ActionResult.SUCCESS

        return ActionResult.SUCCESS
    }
}