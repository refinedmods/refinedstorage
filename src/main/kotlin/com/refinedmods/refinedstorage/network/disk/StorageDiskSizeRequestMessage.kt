package com.refinedmods.refinedstorage.network.disk

import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.fml.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

class StorageDiskSizeRequestMessage(private val id: UUID) {
    companion object {
        fun decode(buf: PacketByteBuf): StorageDiskSizeRequestMessage {
            return StorageDiskSizeRequestMessage(buf.readUniqueId())
        }

        fun encode(message: StorageDiskSizeRequestMessage, buf: PacketByteBuf) {
            buf.writeUniqueId(message.id)
        }

        fun handle(message: StorageDiskSizeRequestMessage, ctx: Supplier<NetworkEvent.Context>) {
            ctx.get().enqueueWork({
                val disk = instance().getStorageDiskManager(ctx.get().getSender().getServerWorld())!![message.id]
                if (disk != null) {
                    RS.NETWORK_HANDLER.sendTo(ctx.get().getSender(), StorageDiskSizeResponseMessage(message.id, disk.getStored(), disk.capacity))
                }
            })
            ctx.get().setPacketHandled(true)
        }
    }
}