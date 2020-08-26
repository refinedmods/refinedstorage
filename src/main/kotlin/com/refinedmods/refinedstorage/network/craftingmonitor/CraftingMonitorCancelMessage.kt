package com.refinedmods.refinedstorage.network.craftingmonitor

import com.refinedmods.refinedstorage.container.CraftingMonitorContainer
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.fml.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

class CraftingMonitorCancelMessage(@field:Nullable @param:Nullable private val taskId: UUID?) {
    companion object {
        fun decode(buf: PacketByteBuf): CraftingMonitorCancelMessage {
            return CraftingMonitorCancelMessage(if (buf.readBoolean()) buf.readUniqueId() else null)
        }

        fun encode(message: CraftingMonitorCancelMessage, buf: PacketByteBuf) {
            buf.writeBoolean(message.taskId != null)
            if (message.taskId != null) {
                buf.writeUniqueId(message.taskId)
            }
        }

        fun handle(message: CraftingMonitorCancelMessage, ctx: Supplier<NetworkEvent.Context>) {
            val player: ServerPlayerEntity = ctx.get().getSender()
            if (player != null) {
                ctx.get().enqueueWork({
                    if (player.openContainer is CraftingMonitorContainer) {
                        (player.openContainer as CraftingMonitorContainer).craftingMonitor.onCancelled(player, message.taskId)
                    }
                })
            }
            ctx.get().setPacketHandled(true)
        }
    }
}