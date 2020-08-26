package com.refinedmods.refinedstorage.network.tiledata

import com.refinedmods.refinedstorage.container.BaseContainer
import com.refinedmods.refinedstorage.tile.data.TileDataManager
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.inventory.container.Container
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.BiConsumer
import java.util.function.Supplier

class TileDataParameterUpdateMessage(private val parameter: TileDataParameter<*, *>?, private val value: Any?) {
    companion object {
        fun decode(buf: PacketByteBuf): TileDataParameterUpdateMessage {
            val id: Int = buf.readInt()
            val parameter = TileDataManager.getParameter(id)
            var value: Any? = null
            if (parameter != null) {
                try {
                    value = parameter.getSerializer().read(buf)
                } catch (e: Exception) {
                    // NO OP
                }
            }
            return TileDataParameterUpdateMessage(parameter, value)
        }

        fun encode(message: TileDataParameterUpdateMessage, buf: PacketByteBuf) {
            buf.writeInt(message.parameter!!.id)
            message.parameter.getSerializer().write(buf, message.value)
        }

        fun handle(message: TileDataParameterUpdateMessage, ctx: Supplier<NetworkEvent.Context>) {
            ctx.get().enqueueWork({
                val c: Container = ctx.get().getSender().openContainer
                if (c is BaseContainer) {
                    val consumer: BiConsumer<*, *>? = message.parameter!!.valueConsumer
                    consumer?.accept((c as BaseContainer).tile, message.value)
                }
            })
            ctx.get().setPacketHandled(true)
        }
    }
}