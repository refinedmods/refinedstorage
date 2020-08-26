package com.refinedmods.refinedstorage.util

import com.refinedmods.refinedstorage.api.storage.AccessType
import net.minecraft.nbt.CompoundTag

object AccessTypeUtils {
    private const val NBT_ACCESS_TYPE = "AccessType"
    fun writeAccessType(tag: CompoundTag, type: AccessType) {
        tag.putInt(NBT_ACCESS_TYPE, type.getId())
    }

    fun readAccessType(tag: CompoundTag): AccessType {
        return if (tag.contains(NBT_ACCESS_TYPE)) getAccessType(tag.getInt(NBT_ACCESS_TYPE)) else AccessType.INSERT_EXTRACT
    }

    fun getAccessType(id: Int): AccessType {
        for (type in AccessType.values()) {
            if (type.getId() == id) {
                return type
            }
        }
        return AccessType.INSERT_EXTRACT
    }
}