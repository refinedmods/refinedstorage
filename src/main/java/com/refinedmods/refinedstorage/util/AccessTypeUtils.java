package com.refinedmods.refinedstorage.util;

import com.refinedmods.refinedstorage.api.storage.AccessType;
import net.minecraft.nbt.CompoundTag;

public final class AccessTypeUtils {
    private static final String NBT_ACCESS_TYPE = "AccessType";

    private AccessTypeUtils() {
    }

    public static void writeAccessType(CompoundTag tag, AccessType type) {
        tag.putInt(NBT_ACCESS_TYPE, type.getId());
    }

    public static AccessType readAccessType(CompoundTag tag) {
        return tag.contains(NBT_ACCESS_TYPE) ? getAccessType(tag.getInt(NBT_ACCESS_TYPE)) : AccessType.INSERT_EXTRACT;
    }

    public static AccessType getAccessType(int id) {
        for (AccessType type : AccessType.values()) {
            if (type.getId() == id) {
                return type;
            }
        }

        return AccessType.INSERT_EXTRACT;
    }
}
