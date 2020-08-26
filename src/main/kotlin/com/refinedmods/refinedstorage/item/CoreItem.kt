package com.refinedmods.refinedstorage.item

import com.refinedmods.refinedstorage.RS
import com.thinkslynk.fabric.annotations.registry.RegisterBlockItem
import net.fabricmc.fabric.api.item.v1.FabricItemSettings
import net.minecraft.item.Item

class CoreItem(type: Type) : Item(Settings().group(RS.MAIN_GROUP)) {
    enum class Type {
        CONSTRUCTION, DESTRUCTION
    }

    init {
        this.setRegistryName(RS.ID, if (type == Type.CONSTRUCTION) "construction_core" else "destruction_core")
    }
}