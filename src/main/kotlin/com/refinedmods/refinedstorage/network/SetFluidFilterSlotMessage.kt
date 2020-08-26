package com.refinedmods.refinedstorage.network

import com.refinedmods.refinedstorage.api.util.IComparer
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.Slot
import net.minecraft.network.PacketByteBuf
import net.minecraftforge.fluids.FluidInstance
import net.minecraftforge.fml.network.NetworkEvent
import java.util.function.Supplier

class SetFluidFilterSlotMessage(private val containerSlot: Int, stack: FluidInstance) {
    private val stack: FluidInstance

    companion object {
        fun decode(buf: PacketByteBuf): SetFluidFilterSlotMessage {
            return SetFluidFilterSlotMessage(buf.readInt(), FluidInstance.readFromPacket(buf))
        }

        fun encode(message: SetFluidFilterSlotMessage, buf: PacketByteBuf) {
            buf.writeInt(message.containerSlot)
            message.stack.writeToPacket(buf)
        }

        fun handle(message: SetFluidFilterSlotMessage, ctx: Supplier<NetworkEvent.Context>) {
            val player: PlayerEntity = ctx.get().getSender()
            if (player != null) {
                ctx.get().enqueueWork({
                    val container: Container = player.openContainer
                    if (container != null) {
                        if (message.containerSlot >= 0 && message.containerSlot < container.inventorySlots.size()) {
                            val slot: Slot = container.getSlot(message.containerSlot)
                            if (slot is FluidFilterSlot) {
                                val fluidFilterSlot = slot as FluidFilterSlot

                                // Avoid resetting allowed tag list in the pattern grid.
                                if (instance().getComparer().isEqual(fluidFilterSlot.fluidInventory.getFluid(slot.getSlotIndex()), message.stack, IComparer.COMPARE_NBT)) {
                                    fluidFilterSlot.fluidInventory.getFluid(slot.getSlotIndex()).setAmount(message.stack.getAmount())
                                    fluidFilterSlot.fluidInventory.onChanged(slot.getSlotIndex())
                                } else {
                                    fluidFilterSlot.fluidInventory.setFluid(slot.getSlotIndex(), message.stack)
                                }
                            }
                        }
                    }
                })
            }
            ctx.get().setPacketHandled(true)
        }
    }

    init {
        this.stack = stack
    }
}