package com.refinedmods.refinedstorage.network.tiledata

import com.refinedmods.refinedstorage.tile.data.TileDataManager
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.network.PacketByteBuf
import net.minecraft.tileentity.BlockEntity
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

class TileDataParameterMessage(tile: BlockEntity?, parameter: TileDataParameter<*, *>?, initial: Boolean) {
    private val tile: BlockEntity?
    private val parameter: TileDataParameter<*, *>?
    private val initial: Boolean

    companion object {
        fun decode(buf: PacketByteBuf): TileDataParameterMessage {
            val id: Int = buf.readInt()
            val initial: Boolean = buf.readBoolean()
            val parameter = TileDataManager.getParameter(id)
            if (parameter != null) {
                try {
                    parameter.setValue(initial, parameter.getSerializer().read(buf))
                } catch (e: Exception) {
                    // NO OP
                }
            }
            return TileDataParameterMessage(null, null, initial)
        }

        fun encode(message: TileDataParameterMessage, buf: PacketByteBuf) {
            buf.writeInt(message.parameter!!.id)
            buf.writeBoolean(message.initial)
            message.parameter.getSerializer().write(buf, message.parameter.valueProducer.apply(message.tile))
        }

        fun handle(message: TileDataParameterMessage?, ctx: Supplier<NetworkEvent.Context>) {
            ctx.get().setPacketHandled(true)
        }
    }

    init {
        this.tile = tile
        this.parameter = parameter
        this.initial = initial
    }
}