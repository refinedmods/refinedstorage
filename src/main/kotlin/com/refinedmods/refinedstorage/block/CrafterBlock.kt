package com.refinedmods.refinedstorage.block

import com.refinedmods.refinedstorage.RS
//import com.refinedmods.refinedstorage.tile.CrafterTile
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

@RegisterBlock(RS.ID, CrafterBlock.ID)
@RegisterBlockItem(RS.ID, CrafterBlock.ID, "MISC")
class CrafterBlock:
        NetworkNodeBlock(BlockUtils.DEFAULT_ROCK_PROPERTIES)
//        BlockEntityProvider
{
    companion object {
        const val ID = "crafter"
    }
    override val direction: BlockDirection
        get() = BlockDirection.ANY_FACE_PLAYER

    init {
        defaultState = defaultState.with(CONNECTED, false)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(CONNECTED)
    }

    //    override fun createBlockEntity(world: BlockView): BlockEntity? {
//        // TODO BlockEntities
//        return NoOpBlockEntity()
////        return CrafterTile()
//    }

    // TODO BlockEntities
//    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack) {
//        super.onPlaced(world, pos, state, placer, itemStack)
//        if (!world.isClient) {
//            val tile: BlockEntity? = world.getBlockEntity(pos)
//            if (tile is CrafterTile && itemStack.hasCustomName()) {
//                (tile as CrafterTile?)!!.node!!.displayName = itemStack.name
//                (tile as CrafterTile?)!!.node!!.markDirty()
//            }
//        }
//    }

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        //TODO  Port GUI
//        return if (!world.isClient) {
//            NetworkUtils.attempt(world, pos, hit.getFace(), player, Runnable {
//                NetworkHooks.openGui(
//                        player as ServerPlayerEntity?,
//                        PositionalTileContainerProvider<CrafterTile>(
//                                (world.getBlockEntity(pos) as CrafterTile?)!!.node.name,
//                                { tile: CrafterTile?, windowId: Int, inventory: PlayerInventory?, p: PlayerEntity? -> CrafterContainer(tile, player, windowId) },
//                                pos
//                        ),
//                        pos
//                )
//            }, Permission.MODIFY, Permission.AUTOCRAFTING)
//        } else ActionResult.SUCCESS

        return ActionResult.SUCCESS
    }
}