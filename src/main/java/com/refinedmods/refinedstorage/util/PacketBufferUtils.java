package com.refinedmods.refinedstorage.util;

import net.minecraft.network.PacketBuffer;

public class PacketBufferUtils {
    // @Volatile: From PacketBuffer#readString, this exists because SideOnly
    public static String readString(PacketBuffer buffer) {
        return buffer.readString(32767);
    }
}
