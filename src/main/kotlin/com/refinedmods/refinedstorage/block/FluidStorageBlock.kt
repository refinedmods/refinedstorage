package com.refinedmods.refinedstorage.block

import com.refinedmods.refinedstorage.RS
//import com.refinedmods.refinedstorage.apiimpl.network.node.storage.FluidStorageNetworkNode
import com.refinedmods.refinedstorage.apiimpl.storage.FluidStorageType
//import com.refinedmods.refinedstorage.tile.FluidStorageTile
import com.refinedmods.refinedstorage.tile.NoOpBlockEntity
import com.refinedmods.refinedstorage.util.BlockUtils
import com.thinkslynk.fabric.annotations.registry.RegisterBlock
import net.minecraft.block.BlockEntityProvider
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.BlockView
import net.minecraft.world.World

open class FluidStorageBlock(val type: FluidStorageType):
        NetworkNodeBlock(BlockUtils.DEFAULT_ROCK_PROPERTIES, false) // TODO Double check connected value
//        BlockEntityProvider
{
    companion object {
        const val SIXTY_FOUR_K_FLUID_STORAGE_BLOCK_ID = "64k_fluid_storage_block"
        const val TWO_HUNDRED_FIFTY_SIX_K_FLUID_STORAGE_BLOCK_ID = "256k_fluid_storage_block"
        const val THOUSAND_TWENTY_FOUR_K_FLUID_STORAGE_BLOCK_ID = "1024k_fluid_storage_block"
        const val FOUR_THOUSAND_NINETY_SIX_K_FLUID_STORAGE_BLOCK_ID = "4096k_fluid_storage_block"
        const val CREATIVE_FLUID_STORAGE_BLOCK_ID = "creative_fluid_storage_block"
    }

//    override fun createBlockEntity(world: BlockView?): BlockEntity?
//            = NoOpBlockEntity()
    // TODO BlockEntities
//            = FluidStorageTile(type)

    // TODO Network
//    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, placer: LivingEntity?, itemStack: ItemStack) {
//        if (!world.isClient) {
//            (world.getBlockEntity(pos) as FluidStorageTile?)?.node?.let {
//                itemStack.tag?.let { tag ->
//                    if (tag.containsUuid(FluidStorageNetworkNode.NBT_ID)) {
//                        it.storageId = tag.getUuid(FluidStorageNetworkNode.NBT_ID)
//                    }
//                    it.loadStorage()
//                }
//            }
//        }
//
//        // Call this after loading the storage, so the network discovery can use the loaded storage.
//        super.onPlaced(world, pos, state, placer, itemStack)
//    }

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        // TODO Port Gui
//        return if (!world.isClient) {
//            NetworkUtils.attemptModify(world, pos, hit.getFace(), player) {
//                NetworkHooks.openGui(player as ServerPlayerEntity?, PositionalTileContainerProvider<FluidStorageTile>(
//                        (world.getBlockEntity(pos) as FluidStorageTile?)!!.node.title,
//                        { tile: FluidStorageTile?, windowId: Int, inventory: PlayerInventory?, p: PlayerEntity? -> FluidStorageContainer(tile, player, windowId) },
//                        pos
//                ), pos)
//            }
//        } else ActionResult.SUCCESS
        return ActionResult.SUCCESS
    }
}

@RegisterBlock(RS.ID, FluidStorageBlock.SIXTY_FOUR_K_FLUID_STORAGE_BLOCK_ID)
class SixtyFourKFluidStorageBlock: FluidStorageBlock(FluidStorageType.SIXTY_FOUR_K)
@RegisterBlock(RS.ID, FluidStorageBlock.TWO_HUNDRED_FIFTY_SIX_K_FLUID_STORAGE_BLOCK_ID)
class TwoHundredFiftySixKFluidStorageBlock: FluidStorageBlock(FluidStorageType.TWO_HUNDRED_FIFTY_SIX_K)
@RegisterBlock(RS.ID, FluidStorageBlock.THOUSAND_TWENTY_FOUR_K_FLUID_STORAGE_BLOCK_ID)
class ThousandFourKFluidStorageBlock: FluidStorageBlock(FluidStorageType.THOUSAND_TWENTY_FOUR_K)
@RegisterBlock(RS.ID, FluidStorageBlock.FOUR_THOUSAND_NINETY_SIX_K_FLUID_STORAGE_BLOCK_ID)
class FourThousandNinetySixKFluidStorageBlock: FluidStorageBlock(FluidStorageType.FOUR_THOUSAND_NINETY_SIX_K)
@RegisterBlock(RS.ID, FluidStorageBlock.CREATIVE_FLUID_STORAGE_BLOCK_ID)
class CreativeFluidStorageBlock: FluidStorageBlock(FluidStorageType.CREATIVE)