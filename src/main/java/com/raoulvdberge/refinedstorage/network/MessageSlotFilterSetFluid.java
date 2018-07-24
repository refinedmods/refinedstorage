package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilterFluid;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageSlotFilterSetFluid extends MessageHandlerPlayerToServer<MessageSlotFilterSetFluid> implements IMessage {
    private int containerSlot;
    private FluidStack stack;

    public MessageSlotFilterSetFluid(int containerSlot, FluidStack stack) {
        this.containerSlot = containerSlot;
        this.stack = stack;
    }

    public MessageSlotFilterSetFluid() {
        // NO OP
    }

    @Override
    protected void handle(MessageSlotFilterSetFluid message, EntityPlayerMP player) {
        Container container = player.openContainer;

        if (container != null) {
            if (message.containerSlot >= 0 && message.containerSlot < container.inventorySlots.size()) {
                Slot slot = container.getSlot(message.containerSlot);

                if (slot instanceof SlotFilterFluid) {
                    ((SlotFilterFluid) slot).getFluidInventory().setFluid(slot.getSlotIndex(), message.stack);
                }
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        containerSlot = buf.readInt();
        stack = StackUtils.readFluidStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(containerSlot);
        StackUtils.writeFluidStack(buf, stack);
    }
}
