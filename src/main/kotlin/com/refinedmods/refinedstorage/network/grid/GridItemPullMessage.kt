package com.refinedmods.refinedstorage.network.grid

import com.refinedmods.refinedstorage.container.GridContainer
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.inventory.container.Container
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.fml.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

class GridItemPullMessage(private val id: UUID, private val flags: Int) {
    companion object {
        fun decode(buf: PacketByteBuf): GridItemPullMessage {
            return GridItemPullMessage(buf.readUniqueId(), buf.readInt())
        }

        fun encode(message: GridItemPullMessage, buf: PacketByteBuf) {
            buf.writeUniqueId(message.id)
            buf.writeInt(message.flags)
        }

        fun handle(message: GridItemPullMessage, ctx: Supplier<NetworkEvent.Context>) {
            val player: ServerPlayerEntity = ctx.get().getSender()
            if (player != null) {
                ctx.get().enqueueWork({
                    val container: Container = player.openContainer
                    if (container is GridContainer) {
                        val grid = (container as GridContainer).grid
                        if (grid!!.itemHandler != null) {
                            grid.itemHandler!!.onExtract(player, message.id, message.flags)
                        }
                    }
                })
            }
            ctx.get().setPacketHandled(true)
        }
    }
}