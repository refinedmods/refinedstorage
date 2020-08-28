package com.refinedmods.refinedstorage.network.disk

import net.minecraft.network.PacketByteBuf
import java.util.*

class StorageDiskSizeResponseMessage(private val id: UUID, private val stored: Int, private val capacity: Int) {
    companion object {
        fun encode(message: StorageDiskSizeResponseMessage, buf: PacketByteBuf) {
            buf.writeUuid(message.id)
            buf.writeInt(message.stored)
            buf.writeInt(message.capacity)
        }

        fun decode(buf: PacketByteBuf): StorageDiskSizeResponseMessage {
            return StorageDiskSizeResponseMessage(buf.readUuid(), buf.readInt(), buf.readInt())
        }

//        fun handle(message: StorageDiskSizeResponseMessage, ctx: Supplier<NetworkEvent.Context>) {
//            ctx.get().enqueueWork({ (instance().getStorageDiskSync() as StorageDiskSync?).setData(message.id, StorageDiskSyncData(message.stored, message.capacity)) })
//            ctx.get().setPacketHandled(true)
//        }
    }
}