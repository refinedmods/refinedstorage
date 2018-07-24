package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilterFluid;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventory;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageSlotFilterFluidSetAmount extends MessageHandlerPlayerToServer<MessageSlotFilterFluidSetAmount> implements IMessage {
    private int containerSlot;
    private int amount;

    public MessageSlotFilterFluidSetAmount(int containerSlot, int amount) {
        this.containerSlot = containerSlot;
        this.amount = amount;
    }

    public MessageSlotFilterFluidSetAmount() {
        // NO OP
    }

    @Override
    protected void handle(MessageSlotFilterFluidSetAmount message, EntityPlayerMP player) {
        Container container = player.openContainer;

        if (container != null) {
            if (message.containerSlot >= 0 && message.containerSlot < container.inventorySlots.size()) {
                Slot slot = container.getSlot(message.containerSlot);

                if (slot instanceof SlotFilterFluid) {
                    FluidInventory inventory = ((SlotFilterFluid) slot).getFluidInventory();

                    FluidStack stack = inventory.getFluid(slot.getSlotIndex());

                    if (stack != null && message.amount > 0 && message.amount <= inventory.getMaxAmount()) {
                        inventory.setFluid(slot.getSlotIndex(), StackUtils.copy(stack, message.amount));
                    }
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
