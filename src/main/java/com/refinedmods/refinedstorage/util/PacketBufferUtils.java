package com.refinedmods.refinedstorage.util;

import net.minecraft.network.PacketBuffer;

public final class PacketBufferUtils {
    private PacketBufferUtils() {
    }

    // @Volatile: From PacketBuffer#readString, this exists because SideOnly
    public static String readString(PacketBuffer buffer) {
        return buffer.readUtf(32767);
    }
}
