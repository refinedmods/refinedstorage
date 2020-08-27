package com.refinedmods.refinedstorage.entity.grid.portable

import net.minecraft.util.StringIdentifiable

enum class PortableGridDiskState(val id: Int, val string: String): StringIdentifiable {
    NORMAL(0, "normal"),
    NEAR_CAPACITY(1, "near_capacity"),
    FULL(2, "full"),
    DISCONNECTED(3, "disconnected"),
    NONE(4, "none");

    override fun asString(): String {
        return string
    }
}