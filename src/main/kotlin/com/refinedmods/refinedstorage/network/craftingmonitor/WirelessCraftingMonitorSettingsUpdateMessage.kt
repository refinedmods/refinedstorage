package com.refinedmods.refinedstorage.network.craftingmonitor

import com.refinedmods.refinedstorage.container.CraftingMonitorContainer
import com.refinedmods.refinedstorage.tile.craftingmonitor.WirelessCraftingMonitor
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.fml.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

class WirelessCraftingMonitorSettingsUpdateMessage(private val tabSelected: Optional<UUID?>, private val tabPage: Int) {
    companion object {
        fun decode(buf: PacketByteBuf): WirelessCraftingMonitorSettingsUpdateMessage {
            var tabSelected: Optional<UUID?> = Optional.empty()
            if (buf.readBoolean()) {
                tabSelected = Optional.of(buf.readUniqueId())
            }
            val tabPage: Int = buf.readInt()
            return WirelessCraftingMonitorSettingsUpdateMessage(tabSelected, tabPage)
        }

        fun encode(message: WirelessCraftingMonitorSettingsUpdateMessage, buf: PacketByteBuf) {
            buf.writeBoolean(message.tabSelected.isPresent)
            message.tabSelected.ifPresent(buf::writeUniqueId)
            buf.writeInt(message.tabPage)
        }

        fun handle(message: WirelessCraftingMonitorSettingsUpdateMessage, ctx: Supplier<NetworkEvent.Context>) {
            val player: ServerPlayerEntity = ctx.get().getSender()
            if (player != null) {
                ctx.get().enqueueWork({
                    if (player.openContainer is CraftingMonitorContainer) {
                        ((player.openContainer as CraftingMonitorContainer).craftingMonitor as WirelessCraftingMonitor).setSettings(message.tabSelected, message.tabPage)
                    }
                })
            }
            ctx.get().setPacketHandled(true)
        }
    }
}