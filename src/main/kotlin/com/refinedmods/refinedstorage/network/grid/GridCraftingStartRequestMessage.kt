package com.refinedmods.refinedstorage.network.grid

import com.refinedmods.refinedstorage.container.GridContainer
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.inventory.container.Container
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.fml.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

class GridCraftingStartRequestMessage(private val id: UUID, private val quantity: Int, private val fluids: Boolean) {
    companion object {
        fun decode(buf: PacketByteBuf): GridCraftingStartRequestMessage {
            return GridCraftingStartRequestMessage(
                    buf.readUniqueId(),
                    buf.readInt(),
                    buf.readBoolean()
            )
        }

        fun encode(message: GridCraftingStartRequestMessage, buf: PacketByteBuf) {
            buf.writeUniqueId(message.id)
            buf.writeInt(message.quantity)
            buf.writeBoolean(message.fluids)
        }

        fun handle(message: GridCraftingStartRequestMessage, ctx: Supplier<NetworkEvent.Context>) {
            val player: ServerPlayerEntity = ctx.get().getSender()
            if (player != null) {
                ctx.get().enqueueWork({
                    val container: Container = player.openContainer
                    if (container is GridContainer) {
                        val grid = (container as GridContainer).grid
                        if (message.fluids) {
                            if (grid!!.fluidHandler != null) {
                                grid.fluidHandler!!.onCraftingRequested(player, message.id, message.quantity)
                            }
                        } else {
                            if (grid!!.itemHandler != null) {
                                grid.itemHandler!!.onCraftingRequested(player, message.id, message.quantity)
                            }
                        }
                    }
                })
            }
            ctx.get().setPacketHandled(true)
        }
    }
}