package com.refinedmods.refinedstorage.network.tiledata

import com.refinedmods.refinedstorage.tile.data.TileDataManager
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.block.entity.BlockEntity
import net.minecraft.network.PacketByteBuf

class TileDataParameterUpdateMessage<T, E: BlockEntity?>(
        private val parameter: TileDataParameter<T?, E?>?,
        private val value: T?
) {
    companion object {
        fun <T: Any?, E: BlockEntity?> decode(buf: PacketByteBuf): TileDataParameterUpdateMessage<T, E> {
            val id: Int = buf.readInt()
            val parameter = TileDataManager.getParameter<T, E>(id)
            var value: T? = null
            if (parameter != null) {
                try {
                    value = parameter.serializer.read(buf)
                } catch (e: Exception) {
                    // NO OP
                }
            }
            return TileDataParameterUpdateMessage<T, E>(parameter, value)
        }

        fun <T, E: BlockEntity> encode(message: TileDataParameterUpdateMessage<T, E>, buf: PacketByteBuf) {
            message.parameter?.let {
                buf.writeInt(it.id)
                it.serializer.write(buf, message.value)
            }
        }

        // TODO Not sure what the equivalent is for NetworkEvents
//        fun <T, E: BlockEntity> handle(message: TileDataParameterUpdateMessage<T, E>, ctx: Supplier<NetworkEvent.Context>) {
//
//            ctx.get().enqueueWork({
//                val c: Container = ctx.get().getSender().openContainer
//                if (c is BaseContainer) {
//                    val consumer: BiConsumer<*, *>? = message.parameter.valueConsumer
//                    consumer?.accept((c as BaseContainer).tile, message.value)
//                }
//            })
//            ctx.get().setPacketHandled(true)
//        }
    }
}