package com.refinedmods.refinedstorage.network.grid

import com.refinedmods.refinedstorage.container.GridContainer
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.inventory.container.Container
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.fml.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

class GridFluidPullMessage(private val id: UUID, private val shift: Boolean) {
    companion object {
        fun decode(buf: PacketByteBuf): GridFluidPullMessage {
            return GridFluidPullMessage(buf.readUniqueId(), buf.readBoolean())
        }

        fun encode(message: GridFluidPullMessage, buf: PacketByteBuf) {
            buf.writeUniqueId(message.id)
            buf.writeBoolean(message.shift)
        }

        fun handle(message: GridFluidPullMessage, ctx: Supplier<NetworkEvent.Context>) {
            val player: ServerPlayerEntity = ctx.get().getSender()
            if (player != null) {
                ctx.get().enqueueWork({
                    val container: Container = player.openContainer
                    if (container is GridContainer) {
                        val grid = (container as GridContainer).grid
                        if (grid!!.fluidHandler != null) {
                            grid.fluidHandler!!.onExtract(player, message.id, message.shift)
                        }
                    }
                })
            }
            ctx.get().setPacketHandled(true)
        }
    }
}