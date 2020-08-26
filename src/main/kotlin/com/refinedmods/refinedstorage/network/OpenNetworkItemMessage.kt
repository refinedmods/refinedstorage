package com.refinedmods.refinedstorage.network

import com.refinedmods.refinedstorage.api.network.INetwork
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.apiimpl.network.grid.factory.PortableGridGridFactory
import com.refinedmods.refinedstorage.item.NetworkItem
import com.refinedmods.refinedstorage.item.blockitem.PortableGridBlockItem
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Consumer
import java.util.function.Supplier

class OpenNetworkItemMessage(private val slotId: Int) {
    companion object {
        fun decode(buf: PacketByteBuf): OpenNetworkItemMessage {
            return OpenNetworkItemMessage(buf.readInt())
        }

        fun encode(message: OpenNetworkItemMessage, buf: PacketByteBuf) {
            buf.writeInt(message.slotId)
        }

        fun handle(message: OpenNetworkItemMessage, ctx: Supplier<NetworkEvent.Context>) {
            val player: ServerPlayerEntity = ctx.get().getSender()
            if (player != null) {
                ctx.get().enqueueWork({
                    val stack: ItemStack = player.inventory.getStackInSlot(message.slotId)
                    if (stack.item is NetworkItem) {
                        (stack.item as NetworkItem).applyNetwork(player.getServer(), stack, Consumer { n: INetwork -> n.networkItemManager!!.open(player, stack, message.slotId) }, Consumer<error.NonExistentClass?> { err: error.NonExistentClass? -> player.sendMessage(err, player.getUniqueID()) })
                    } else if (stack.item is PortableGridBlockItem) {
                        instance().getGridManager()!!.openGrid(PortableGridGridFactory.ID, player, stack, message.slotId)
                    }
                })
            }
            ctx.get().setPacketHandled(true)
        }
    }
}