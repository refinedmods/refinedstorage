package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilterFluid;
import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageSlotFilterFluidUpdate implements IMessage, IMessageHandler<MessageSlotFilterFluidUpdate, IMessage> {
    private int containerSlot;
    private FluidStack stack;

    public MessageSlotFilterFluidUpdate(int containerSlot, FluidStack stack) {
        this.containerSlot = containerSlot;
        this.stack = stack;
    }

    public MessageSlotFilterFluidUpdate() {
        // NO OP
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        containerSlot = buf.readInt();

        if (buf.readBoolean()) {
            stack = StackUtils.readFluidStack(buf);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(containerSlot);

        if (stack == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);

            StackUtils.writeFluidStack(buf, stack);
        }
    }

    @Override
    public IMessage onMessage(MessageSlotFilterFluidUpdate message, MessageContext ctx) {
        GuiBase.executeLater(gui -> {
            if (message.containerSlot >= 0 && message.containerSlot < gui.inventorySlots.inventorySlots.size()) {
                Slot slot = gui.inventorySlots.getSlot(message.containerSlot);

                if (slot instanceof SlotFilterFluid) {
                    ((SlotFilterFluid) slot).getFluidInventory().setFluid(slot.getSlotIndex(), message.stack);
                }
            }
        });

        return null;
    }
}
