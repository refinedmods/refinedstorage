package com.refinedmods.refinedstorage.item

import com.refinedmods.refinedstorage.RS
import net.minecraft.item.Item

class ProcessorItem(type: Type) : Item(Properties().group(RS.MAIN_GROUP)) {
    enum class Type(override val name: String) {
        RAW_BASIC("raw_basic"), RAW_IMPROVED("raw_improved"), RAW_ADVANCED("raw_advanced"), BASIC("basic"), IMPROVED("improved"), ADVANCED("advanced");
    }

    init {
        this.setRegistryName(RS.ID, type.name + "_processor")
    }
}