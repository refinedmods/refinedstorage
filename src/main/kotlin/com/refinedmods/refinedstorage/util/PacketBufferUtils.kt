package com.refinedmods.refinedstorage.util

import net.minecraft.network.PacketByteBuf

object PacketByteBufUtils {
    // @Volatile: From PacketByteBuf#readString, this exists because SideOnly
    fun readString(buffer: PacketByteBuf): String {
        return buffer.readString(32767)
    }
}