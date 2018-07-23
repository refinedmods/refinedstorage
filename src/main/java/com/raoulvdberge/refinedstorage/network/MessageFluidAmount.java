package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.container.slot.SlotFilterItemOrFluid;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageFluidAmount extends MessageHandlerPlayerToServer<MessageFluidAmount> implements IMessage {
    private int containerSlot;
    private int amount;

    public MessageFluidAmount(int containerSlot, int amount) {
        this.containerSlot = containerSlot;
        this.amount = amount;
    }

    public MessageFluidAmount() {
        // NO OP
    }

    @Override
    protected void handle(MessageFluidAmount message, EntityPlayerMP player) {
        Container container = player.openContainer;

        if (container != null) {
            if (message.containerSlot >= 0 && message.containerSlot < container.inventorySlots.size()) {
                Slot slot = container.getSlot(message.containerSlot);

                if (slot instanceof SlotFilterItemOrFluid && ((SlotFilterItemOrFluid) slot).getFluidAmountChangeListener() != null) {
                    ((SlotFilterItemOrFluid) slot).getFluidAmountChangeListener().onChangeRequested(slot.getSlotIndex(), message.amount);
                }
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        containerSlot = buf.readInt();
        amount = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(containerSlot);
        buf.writeInt(amount);
    }
}
