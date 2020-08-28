package com.refinedmods.refinedstorage.block

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.util.BlockUtils
import com.thinkslynk.fabric.annotations.registry.RegisterBlock
import com.thinkslynk.fabric.annotations.registry.RegisterBlockItem
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

@RegisterBlock(RS.ID, DiskDriveBlock.ID)
@RegisterBlockItem(RS.ID, DiskDriveBlock.ID, "MISC")
class DiskDriveBlock:
        NetworkNodeBlock(BlockUtils.DEFAULT_ROCK_PROPERTIES) // TODO Double check connected value
//        BlockEntityProvider
{
    companion object {
        const val ID = "disk_drive"
    }
    override val direction: BlockDirection
        get() = BlockDirection.HORIZONTAL

//    override fun createBlockEntity(world: BlockView): BlockEntity?
//            = NoOpBlockEntity()
//    // TODO BlockEntities
////            = DiskDriveTile()

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        // TODO Port Gui
//        return if (!world.isClient) {
//            NetworkUtils.attemptModify(world, pos, rayTraceResult.getFace(), player) {
//                NetworkHooks.openGui(
//                        player as ServerPlayerEntity?,
//                        PositionalTileContainerProvider<DiskDriveTile>(
//                                TranslationTextComponent("gui.refinedstorage.disk_drive"),
//                                { tile: DiskDriveTile?, windowId: Int, inventory: PlayerInventory?, p: PlayerEntity? -> DiskDriveContainer(tile, p, windowId) },
//                                pos
//                        ),
//                        pos
//                )
//            }
//        } else ActionResult.SUCCESS

        return ActionResult.SUCCESS
    }

}