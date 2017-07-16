package com.raoulvdberge.refinedstorage.util;

import com.raoulvdberge.refinedstorage.api.storage.AccessType;
import net.minecraft.nbt.NBTTagCompound;

public final class AccessTypeUtils {
    private static final String NBT_ACCESS_TYPE = "AccessType";

    public static void writeAccessType(NBTTagCompound tag, AccessType type) {
        tag.setInteger(NBT_ACCESS_TYPE, type.getId());
    }

    public static AccessType readAccessType(NBTTagCompound tag) {
        return tag.hasKey(NBT_ACCESS_TYPE) ? getAccessType(tag.getInteger(NBT_ACCESS_TYPE)) : AccessType.INSERT_EXTRACT;
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
