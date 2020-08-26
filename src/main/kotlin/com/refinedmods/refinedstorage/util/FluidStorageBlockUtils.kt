package com.refinedmods.refinedstorage.util

import com.refinedmods.refinedstorage.RSTiles
import com.refinedmods.refinedstorage.apiimpl.network.node.storage.FluidStorageNetworkNode
import com.refinedmods.refinedstorage.apiimpl.storage.FluidStorageType
import com.refinedmods.refinedstorage.tile.FluidStorageTile
import net.minecraft.tileentity.BlockEntityType
import net.minecraft.util.Identifier

object FluidStorageBlockUtils {
    fun getNetworkNodeId(type: FluidStorageType): Identifier {
        return when (type) {
            SIXTY_FOUR_K -> FluidStorageNetworkNode.SIXTY_FOUR_K_FLUID_STORAGE_BLOCK_ID
            TWO_HUNDRED_FIFTY_SIX_K -> FluidStorageNetworkNode.TWO_HUNDRED_FIFTY_SIX_K_FLUID_STORAGE_BLOCK_ID
            THOUSAND_TWENTY_FOUR_K -> FluidStorageNetworkNode.THOUSAND_TWENTY_FOUR_K_FLUID_STORAGE_BLOCK_ID
            FOUR_THOUSAND_NINETY_SIX_K -> FluidStorageNetworkNode.FOUR_THOUSAND_NINETY_SIX_K_FLUID_STORAGE_BLOCK_ID
            CREATIVE -> FluidStorageNetworkNode.CREATIVE_FLUID_STORAGE_BLOCK_ID
            else -> throw IllegalArgumentException("Unknown storage type $type")
        }
    }

    fun getBlockEntityType(type: FluidStorageType): BlockEntityType<FluidStorageTile> {
        return when (type) {
            SIXTY_FOUR_K -> RSTiles.SIXTY_FOUR_K_FLUID_STORAGE_BLOCK
            TWO_HUNDRED_FIFTY_SIX_K -> RSTiles.TWO_HUNDRED_FIFTY_SIX_K_FLUID_STORAGE_BLOCK
            THOUSAND_TWENTY_FOUR_K -> RSTiles.THOUSAND_TWENTY_FOUR_K_FLUID_STORAGE_BLOCK
            FOUR_THOUSAND_NINETY_SIX_K -> RSTiles.FOUR_THOUSAND_NINETY_SIX_K_FLUID_STORAGE_BLOCK
            CREATIVE -> RSTiles.CREATIVE_FLUID_STORAGE_BLOCK
            else -> throw IllegalArgumentException("Unknown storage type $type")
        }
    }
}