package com.refinedmods.refinedstorage.util;

import net.minecraft.network.FriendlyByteBuf;

public final class PacketBufferUtils {
    private PacketBufferUtils() {
    }

    // @Volatile: From PacketBuffer#readUtf, this exists because SideOnly
    public static String readString(FriendlyByteBuf buffer) {
        return buffer.readUtf(32767);
    }
}
