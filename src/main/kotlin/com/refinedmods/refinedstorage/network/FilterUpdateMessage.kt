package com.refinedmods.refinedstorage.network

import com.refinedmods.refinedstorage.container.FilterContainer
import com.refinedmods.refinedstorage.item.FilterItem.Companion.setCompare
import com.refinedmods.refinedstorage.item.FilterItem.Companion.setModFilter
import com.refinedmods.refinedstorage.item.FilterItem.Companion.setMode
import com.refinedmods.refinedstorage.item.FilterItem.Companion.setName
import com.refinedmods.refinedstorage.item.FilterItem.Companion.setType
import com.refinedmods.refinedstorage.util.PacketByteBufUtils
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

class FilterUpdateMessage(private val compare: Int, private val mode: Int, private val modFilter: Boolean, private val name: String, private val type: Int) {
    companion object {
        fun decode(buf: PacketByteBuf): FilterUpdateMessage {
            return FilterUpdateMessage(
                    buf.readInt(),
                    buf.readInt(),
                    buf.readBoolean(),
                    PacketByteBufUtils.readString(buf),
                    buf.readInt()
            )
        }

        fun encode(message: FilterUpdateMessage, buf: PacketByteBuf) {
            buf.writeInt(message.compare)
            buf.writeInt(message.mode)
            buf.writeBoolean(message.modFilter)
            buf.writeString(message.name)
            buf.writeInt(message.type)
        }

        fun handle(message: FilterUpdateMessage, ctx: Supplier<NetworkEvent.Context>) {
            val player: PlayerEntity = ctx.get().getSender()
            if (player != null && player.openContainer is FilterContainer) {
                ctx.get().enqueueWork({
                    setCompare((player.openContainer as FilterContainer).stack, message.compare)
                    setMode((player.openContainer as FilterContainer).stack, message.mode)
                    setModFilter((player.openContainer as FilterContainer).stack, message.modFilter)
                    setName((player.openContainer as FilterContainer).stack, message.name)
                    setType((player.openContainer as FilterContainer).stack, message.type)
                })
            }
            ctx.get().setPacketHandled(true)
        }
    }
}