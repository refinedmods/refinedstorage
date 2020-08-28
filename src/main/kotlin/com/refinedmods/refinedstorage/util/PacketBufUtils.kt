package com.refinedmods.refinedstorage.util

import net.minecraft.network.PacketByteBuf
import java.util.*

object PacketBufUtils {

    fun PacketByteBuf.writeOptionalUuid(uuid: UUID?) = writeOptional(uuid) {
        writeUuid(it)
    }

    fun PacketByteBuf.readOptionalUuid() = readOptional() {
        readUuid()
    }

    fun <T : Any> PacketByteBuf.writeOptional(item: T?, block: PacketByteBuf.(T) -> Unit) {
        if (item == null) {
            writeBoolean(false)
        } else {
            writeBoolean(true)
            block(item)
        }
    }


    fun <T : Any> PacketByteBuf.readOptional(block: PacketByteBuf.() -> T): T? {
        if (readBoolean()) {
            return block()
        }
        return null
    }

}