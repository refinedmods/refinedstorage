package com.refinedmods.refinedstorage.network.grid

import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.network.ClientProxy
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraftforge.fml.network.NetworkEvent
import java.util.*
import java.util.function.Supplier

class GridCraftingPreviewResponseMessage(val elements: List<ICraftingPreviewElement<*>>, val id: UUID, val quantity: Int, val isFluids: Boolean) {

    companion object {
        fun decode(buf: PacketByteBuf): GridCraftingPreviewResponseMessage {
            val id: UUID = buf.readUniqueId()
            val quantity: Int = buf.readInt()
            val fluids: Boolean = buf.readBoolean()
            val stacks: MutableList<ICraftingPreviewElement<*>> = LinkedList()
            val size: Int = buf.readInt()
            for (i in 0 until size) {
                val type: Identifier = buf.readIdentifier()
                stacks.add(instance().getCraftingPreviewElementRegistry()!![type].apply(buf))
            }
            return GridCraftingPreviewResponseMessage(stacks, id, quantity, fluids)
        }

        fun encode(message: GridCraftingPreviewResponseMessage, buf: PacketByteBuf) {
            buf.writeUniqueId(message.id)
            buf.writeInt(message.quantity)
            buf.writeBoolean(message.isFluids)
            buf.writeInt(message.elements.size)
            for (stack in message.elements) {
                buf.writeIdentifier(stack.getId())
                stack.write(buf)
            }
        }

        fun handle(message: GridCraftingPreviewResponseMessage, ctx: Supplier<NetworkEvent.Context>) {
            ctx.get().enqueueWork({ ClientProxy.onReceivedCraftingPreviewResponseMessage(message) })
            ctx.get().setPacketHandled(true)
        }
    }
}