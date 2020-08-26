package com.refinedmods.refinedstorage.network.grid

import com.refinedmods.refinedstorage.container.GridContainer
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.inventory.container.Container
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

object GridFluidInsertHeldMessage {
    fun decode(buf: PacketByteBuf?): GridFluidInsertHeldMessage {
        return GridFluidInsertHeldMessage()
    }

    fun encode(message: GridFluidInsertHeldMessage?, buf: PacketByteBuf?) {}
    fun handle(message: GridFluidInsertHeldMessage?, ctx: Supplier<NetworkEvent.Context>) {
        val player: ServerPlayerEntity = ctx.get().getSender()
        if (player != null) {
            ctx.get().enqueueWork({
                val container: Container = player.openContainer
                if (container is GridContainer) {
                    val grid = (container as GridContainer).grid
                    if (grid!!.fluidHandler != null) {
                        grid.fluidHandler!!.onInsertHeldContainer(player)
                    }
                }
            })
        }
        ctx.get().setPacketHandled(true)
    }
}