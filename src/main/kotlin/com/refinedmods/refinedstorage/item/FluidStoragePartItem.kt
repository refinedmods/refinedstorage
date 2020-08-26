package com.refinedmods.refinedstorage.item

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.RSItems
import com.refinedmods.refinedstorage.apiimpl.storage.FluidStorageType
import net.minecraft.item.Item

class FluidStoragePartItem(type: FluidStorageType) : Item(Properties().group(RS.MAIN_GROUP)) {
    companion object {
        fun getByType(type: FluidStorageType): FluidStoragePartItem {
            return when (type) {
                SIXTY_FOUR_K -> RSItems.SIXTY_FOUR_K_FLUID_STORAGE_PART
                TWO_HUNDRED_FIFTY_SIX_K -> RSItems.TWO_HUNDRED_FIFTY_SIX_K_FLUID_STORAGE_PART
                THOUSAND_TWENTY_FOUR_K -> RSItems.THOUSAND_TWENTY_FOUR_K_FLUID_STORAGE_PART
                FOUR_THOUSAND_NINETY_SIX_K -> RSItems.FOUR_THOUSAND_NINETY_SIX_K_FLUID_STORAGE_PART
                else -> throw IllegalArgumentException("Cannot get fluid storage part of $type")
            }
        }
    }

    init {
        this.setRegistryName(RS.ID, type.getName().toString() + "_fluid_storage_part")
    }
}