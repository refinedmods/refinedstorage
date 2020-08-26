package com.refinedmods.refinedstorage.extensions

import net.minecraft.network.PacketByteBuf
import java.io.IOException

interface PacketIO {

    @Throws(IOException::class)
    fun read(buf: PacketByteBuf) {

    }

    @Throws(IOException::class)
    fun write(buf: PacketByteBuf) {

    }
}