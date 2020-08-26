package com.refinedmods.refinedstorage.block

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.tile.DiskManipulatorTile
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

@RegisterBlock(RS.ID, DiskManipulatorBlock.ID)
class DiskManipulatorBlock:
        NetworkNodeBlock(BlockUtils.DEFAULT_ROCK_PROPERTIES, true),
        BlockEntityProvider
{
    companion object {
        const val ID = "disk_manipulator"
    }

    override fun createBlockEntity(world: BlockView): BlockEntity? = DiskManipulatorTile()

    override fun onUse(state: BlockState?, world: World?, pos: BlockPos?, player: PlayerEntity?, hand: Hand?, hit: BlockHitResult?): ActionResult {
        // TODO Port Gui
//        return if (!world.isClient) {
//            NetworkUtils.attemptModify(world, pos, rayTraceResult.getFace(), player) {
//                NetworkHooks.openGui(
//                        player as ServerPlayerEntity?,
//                        PositionalTileContainerProvider<DiskManipulatorTile>(
//                                TranslationTextComponent("gui.refinedstorage.disk_manipulator"),
//                                { tile: DiskManipulatorTile?, windowId: Int, inventory: PlayerInventory?, p: PlayerEntity? -> DiskManipulatorContainer(tile, p, windowId) },
//                                pos
//                        ),
//                        pos
//                )
//            }
//        } else ActionResult.SUCCESS
        return ActionResult.SUCCESS
    }

    override val direction: BlockDirection
        get() = BlockDirection.HORIZONTAL

}