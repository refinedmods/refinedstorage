package com.refinedmods.refinedstorage.network.grid

import com.refinedmods.refinedstorage.network.ClientProxy
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

object GridCraftingStartResponseMessage {
    fun decode(buf: PacketByteBuf?): GridCraftingStartResponseMessage {
        return GridCraftingStartResponseMessage()
    }

    fun encode(message: GridCraftingStartResponseMessage?, buf: PacketByteBuf?) {}
    fun handle(message: GridCraftingStartResponseMessage?, ctx: Supplier<NetworkEvent.Context>) {
        ctx.get().enqueueWork({ ClientProxy.onReceivedCraftingStartResponseMessage() })
        ctx.get().setPacketHandled(true)
    }
}