package com.refinedmods.refinedstorage.network.grid

import com.refinedmods.refinedstorage.container.GridContainer
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.container.Container
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

object GridClearMessage {
    fun decode(buf: PacketByteBuf?): GridClearMessage {
        return GridClearMessage()
    }

    fun encode(message: GridClearMessage?, buf: PacketByteBuf?) {
        // NO OP
    }

    fun handle(message: GridClearMessage?, ctx: Supplier<NetworkEvent.Context>) {
        val player: PlayerEntity = ctx.get().getSender()
        if (player != null) {
            ctx.get().enqueueWork({
                val container: Container = player.openContainer
                if (container is GridContainer) {
                    (container as GridContainer).grid!!.onClear(player)
                }
            })
        }
        ctx.get().setPacketHandled(true)
    }
}