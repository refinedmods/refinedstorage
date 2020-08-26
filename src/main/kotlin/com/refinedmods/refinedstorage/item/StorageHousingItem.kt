package com.refinedmods.refinedstorage.item

import com.refinedmods.refinedstorage.RS
import net.minecraft.item.Item

class StorageHousingItem : Item(Properties().group(RS.MAIN_GROUP)) {
    init {
        this.setRegistryName(RS.ID, "storage_housing")
    }
}