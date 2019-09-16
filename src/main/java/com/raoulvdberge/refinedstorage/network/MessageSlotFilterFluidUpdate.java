package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilterFluid;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageSlotFilterFluidUpdate {
    private int containerSlot;
    private FluidStack stack;

    public MessageSlotFilterFluidUpdate(int containerSlot, FluidStack stack) {
        this.containerSlot = containerSlot;
        this.stack = stack;
    }

    public static void encode(MessageSlotFilterFluidUpdate message, PacketBuffer buf) {
        buf.writeInt(message.containerSlot);
        message.stack.writeToPacket(buf);
    }

    public static MessageSlotFilterFluidUpdate decode(PacketBuffer buf) {
        return new MessageSlotFilterFluidUpdate(buf.readInt(), FluidStack.readFromPacket(buf));
    }

    public static void handle(MessageSlotFilterFluidUpdate message, Supplier<NetworkEvent.Context> ctx) {
        GuiBase.executeLater(gui -> {
            if (message.containerSlot >= 0 && message.containerSlot < gui.getContainer().inventorySlots.size()) {
                Slot slot = gui.getContainer().getSlot(message.containerSlot);

                if (slot instanceof SlotFilterFluid) {
                    ((SlotFilterFluid) slot).getFluidInventory().setFluid(slot.getSlotIndex(), message.stack);
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
