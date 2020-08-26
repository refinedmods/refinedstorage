package com.refinedmods.refinedstorage.network.disk

import com.refinedmods.refinedstorage.api.storage.disk.StorageDiskSyncData
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.apiimpl.storage.disk.StorageDiskSync
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.fml.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

class StorageDiskSizeResponseMessage(private val id: UUID, private val stored: Int, private val capacity: Int) {
    companion object {
        fun encode(message: StorageDiskSizeResponseMessage, buf: PacketByteBuf) {
            buf.writeUniqueId(message.id)
            buf.writeInt(message.stored)
            buf.writeInt(message.capacity)
        }

        fun decode(buf: PacketByteBuf): StorageDiskSizeResponseMessage {
            return StorageDiskSizeResponseMessage(buf.readUniqueId(), buf.readInt(), buf.readInt())
        }

        fun handle(message: StorageDiskSizeResponseMessage, ctx: Supplier<NetworkEvent.Context>) {
            ctx.get().enqueueWork({ (instance().getStorageDiskSync() as StorageDiskSync?).setData(message.id, StorageDiskSyncData(message.stored, message.capacity)) })
            ctx.get().setPacketHandled(true)
        }
    }
}