package com.refinedmods.refinedstorage.item

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.RSItems
import com.refinedmods.refinedstorage.apiimpl.storage.ItemStorageType
import net.minecraft.item.Item

class StoragePartItem(type: ItemStorageType) : Item(Properties().group(RS.MAIN_GROUP)) {
    companion object {
        fun getByType(type: ItemStorageType): StoragePartItem {
            return when (type) {
                ONE_K -> RSItems.ONE_K_STORAGE_PART
                FOUR_K -> RSItems.FOUR_K_STORAGE_PART
                SIXTEEN_K -> RSItems.SIXTEEN_K_STORAGE_PART
                SIXTY_FOUR_K -> RSItems.SIXTY_FOUR_K_STORAGE_PART
                else -> throw IllegalArgumentException("Cannot get storage part of $type")
            }
        }
    }

    init {
        this.setRegistryName(RS.ID, type.getName().toString() + "_storage_part")
    }
}