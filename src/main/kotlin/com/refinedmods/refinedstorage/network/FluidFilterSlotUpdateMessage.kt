package com.refinedmods.refinedstorage.network

import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot
import com.refinedmods.refinedstorage.screen.BaseScreen
import net.minecraft.inventory.container.Slot
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.fluids.FluidInstance
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

class FluidFilterSlotUpdateMessage(private val containerSlot: Int, stack: FluidInstance) {
    private val stack: FluidInstance

    companion object {
        fun encode(message: FluidFilterSlotUpdateMessage, buf: PacketByteBuf) {
            buf.writeInt(message.containerSlot)
            message.stack.writeToPacket(buf)
        }

        fun decode(buf: PacketByteBuf): FluidFilterSlotUpdateMessage {
            return FluidFilterSlotUpdateMessage(buf.readInt(), FluidInstance.readFromPacket(buf))
        }

        fun handle(message: FluidFilterSlotUpdateMessage, ctx: Supplier<NetworkEvent.Context>) {
            BaseScreen.executeLater { gui: ContainerScreen ->
                if (message.containerSlot >= 0 && message.containerSlot < gui.getContainer().inventorySlots.size()) {
                    val slot: Slot = gui.getContainer().getSlot(message.containerSlot)
                    if (slot is FluidFilterSlot) {
                        (slot as FluidFilterSlot).fluidInventory.setFluid(slot.getSlotIndex(), message.stack)
                    }
                }
            }
            ctx.get().setPacketHandled(true)
        }
    }

    init {
        this.stack = stack
    }
}