package com.refinedmods.refinedstorage.util

import com.refinedmods.refinedstorage.RSTiles
import com.refinedmods.refinedstorage.apiimpl.network.node.storage.StorageNetworkNode
import com.refinedmods.refinedstorage.apiimpl.storage.ItemStorageType
import com.refinedmods.refinedstorage.tile.StorageTile
import net.minecraft.tileentity.BlockEntityType
import net.minecraft.util.Identifier

object StorageBlockUtils {
    fun getNetworkNodeId(type: ItemStorageType): Identifier {
        return when (type) {
            ONE_K -> StorageNetworkNode.ONE_K_STORAGE_BLOCK_ID
            FOUR_K -> StorageNetworkNode.FOUR_K_STORAGE_BLOCK_ID
            SIXTEEN_K -> StorageNetworkNode.SIXTEEN_K_STORAGE_BLOCK_ID
            SIXTY_FOUR_K -> StorageNetworkNode.SIXTY_FOUR_K_STORAGE_BLOCK_ID
            CREATIVE -> StorageNetworkNode.CREATIVE_STORAGE_BLOCK_ID
            else -> throw IllegalArgumentException("Unknown storage type $type")
        }
    }

    fun getBlockEntityType(type: ItemStorageType): BlockEntityType<StorageTile> {
        return when (type) {
            ONE_K -> RSTiles.ONE_K_STORAGE_BLOCK
            FOUR_K -> RSTiles.FOUR_K_STORAGE_BLOCK
            SIXTEEN_K -> RSTiles.SIXTEEN_K_STORAGE_BLOCK
            SIXTY_FOUR_K -> RSTiles.SIXTY_FOUR_K_STORAGE_BLOCK
            CREATIVE -> RSTiles.CREATIVE_STORAGE_BLOCK
            else -> throw IllegalArgumentException("Unknown storage type $type")
        }
    }
}