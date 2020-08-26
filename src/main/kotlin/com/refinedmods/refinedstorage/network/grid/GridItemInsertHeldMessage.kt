package com.refinedmods.refinedstorage.network.grid

import com.refinedmods.refinedstorage.container.GridContainer
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.inventory.container.Container
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

class GridItemInsertHeldMessage(private val single: Boolean) {
    companion object {
        fun decode(buf: PacketByteBuf): GridItemInsertHeldMessage {
            return GridItemInsertHeldMessage(buf.readBoolean())
        }

        fun encode(message: GridItemInsertHeldMessage, buf: PacketByteBuf) {
            buf.writeBoolean(message.single)
        }

        fun handle(message: GridItemInsertHeldMessage, ctx: Supplier<NetworkEvent.Context>) {
            val player: ServerPlayerEntity = ctx.get().getSender()
            if (player != null) {
                ctx.get().enqueueWork({
                    val container: Container = player.openContainer
                    if (container is GridContainer) {
                        val grid = (container as GridContainer).grid
                        if (grid!!.itemHandler != null) {
                            grid.itemHandler!!.onInsertHeldItem(player, message.single)
                        }
                    }
                })
            }
            ctx.get().setPacketHandled(true)
        }
    }
}