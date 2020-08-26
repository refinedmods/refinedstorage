package com.refinedmods.refinedstorage.network

import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot
import com.refinedmods.refinedstorage.container.slot.legacy.LegacyFilterSlot
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.Slot
import net.minecraft.item.ItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.fml.network.NetworkEvent
import net.minecraftforge.items.IItemHandler
import java.util.function.Supplier

class SetFilterSlotMessage(private val containerSlot: Int, private val stack: ItemStack) {
    companion object {
        fun decode(buf: PacketByteBuf): SetFilterSlotMessage {
            return SetFilterSlotMessage(buf.readInt(), buf.readItemStack())
        }

        fun encode(message: SetFilterSlotMessage, buf: PacketByteBuf) {
            buf.writeInt(message.containerSlot)
            buf.writeItemStack(message.stack)
        }

        fun handle(message: SetFilterSlotMessage, ctx: Supplier<NetworkEvent.Context>) {
            if (!message.stack.isEmpty && message.stack.count <= message.stack.getMaxStackSize()) {
                val player: PlayerEntity = ctx.get().getSender()
                if (player != null) {
                    ctx.get().enqueueWork({
                        val container: Container = player.openContainer
                        if (container != null) {
                            if (message.containerSlot >= 0 && message.containerSlot < container.inventorySlots.size()) {
                                val slot: Slot = container.getSlot(message.containerSlot)
                                if (slot is FilterSlot || slot is LegacyFilterSlot) {
                                    // Avoid resetting allowed tag list in the pattern grid.
                                    if (instance().getComparer()!!.isEqualNoQuantity(slot.getStack(), message.stack)) {
                                        slot.getStack().setCount(message.stack.count)
                                        if (slot is FilterSlot) {
                                            val itemHandler: IItemHandler = (slot as FilterSlot).getItemHandler()
                                            if (itemHandler is BaseItemHandler) {
                                                (itemHandler as BaseItemHandler).onChanged(slot.getSlotIndex())
                                            }
                                        } else {
                                            slot.inventory.markDirty()
                                        }
                                    } else {
                                        slot.putStack(message.stack)
                                    }
                                }
                            }
                        }
                    })
                }
            }
            ctx.get().setPacketHandled(true)
        }
    }
}