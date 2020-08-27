package com.refinedmods.refinedstorage.block

import com.refinedmods.refinedstorage.RS
//import com.refinedmods.refinedstorage.apiimpl.network.node.storage.StorageNetworkNode
import com.refinedmods.refinedstorage.apiimpl.storage.ItemStorageType
//import com.refinedmods.refinedstorage.apiimpl.storageimport.ItemStorageType
//import com.refinedmods.refinedstorage.container.StorageContainer
//import com.refinedmods.refinedstorage.container.factory.PositionalTileContainerProvider
//import com.refinedmods.refinedstorage.tile.StorageTile
import com.refinedmods.refinedstorage.util.BlockUtils
//import com.refinedmods.refinedstorage.util.NetworkUtils
import com.thinkslynk.fabric.annotations.registry.RegisterBlock
import net.minecraft.block.BlockState
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

open class StorageBlock(val type: ItemStorageType):
        NetworkNodeBlock(BlockUtils.DEFAULT_ROCK_PROPERTIES, false) // TODO Double check connected value...
//        BlockEntityProvider
{
    companion object {
        const val ONE_K_STORAGE_BLOCK_ID = "1k_storage_block"
        const val FOUR_K_STORAGE_BLOCK_ID = "4k_storage_block"
        const val SIXTEEN_K_STORAGE_BLOCK_ID = "16k_storage_block"
        const val SIXTY_FOUR_K_STORAGE_BLOCK_ID = "64k_storage_block"
        const val CREATIVE_STORAGE_BLOCK_ID ="creative_storage_block"
    }

//    override fun createBlockEntity(world: BlockView): BlockEntity?
//            = NoOpBlockEntity()
    // TODO BlockEntities
//            = StorageTile(type)

    // TODO Network
//    override fun onPlaced(world: World, pos: BlockPos, state: BlockState, entity: LivingEntity?, stack: ItemStack) {
//        if (!world.isClient) {
//            val storage = (world.getBlockEntity(pos) as StorageTile?)!!.node!!
//            stack.tag?.let { tag ->
//                if (stack.hasTag() && tag.containsUuid(StorageNetworkNode.NBT_ID)) {
//                    storage.storageId = tag.getUuid(StorageNetworkNode.NBT_ID)
//                }
//            }
//            storage.loadStorage()
//        }
//
//        // Call this after loading the storage, so the network discovery can use the loaded storage.
//        super.onPlaced(world, pos, state, entity, stack)
//    }

    override fun onUse(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, hit: BlockHitResult): ActionResult {
        // TODO Port Gui
//        return if (!world.isClient) {
//            NetworkUtils.attemptModify(world, pos, hit.getFace(), player) {
//                NetworkHooks.openGui(player as ServerPlayerEntity?, PositionalTileContainerProvider<StorageTile>(
//                        (world.getBlockEntity(pos) as StorageTile?)!!.node.title,
//                        { tile: StorageTile?, windowId: Int, inventory: PlayerInventory?, p: PlayerEntity? -> StorageContainer(tile, player, windowId) },
//                        pos
//                ), pos)
//            }
//        } else ActionResult.SUCCESS
        return ActionResult.SUCCESS
    }
}

@RegisterBlock(RS.ID, StorageBlock.ONE_K_STORAGE_BLOCK_ID)
class OneKStorageBlock: StorageBlock(ItemStorageType.ONE_K)
@RegisterBlock(RS.ID, StorageBlock.FOUR_K_STORAGE_BLOCK_ID)
class FourKStorageBlock: StorageBlock(ItemStorageType.FOUR_K)
@RegisterBlock(RS.ID, StorageBlock.SIXTEEN_K_STORAGE_BLOCK_ID)
class SixteenKStorageBlock: StorageBlock(ItemStorageType.SIXTEEN_K)
@RegisterBlock(RS.ID, StorageBlock.SIXTY_FOUR_K_STORAGE_BLOCK_ID)
class SixtyFourKStorageBlock: StorageBlock(ItemStorageType.SIXTY_FOUR_K)
@RegisterBlock(RS.ID, StorageBlock.CREATIVE_STORAGE_BLOCK_ID)
class CreativeStorageBlock: StorageBlock(ItemStorageType.CREATIVE)