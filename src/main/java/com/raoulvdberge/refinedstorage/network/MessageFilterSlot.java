package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.container.slot.SlotFilter;
import com.raoulvdberge.refinedstorage.container.slot.SlotFilterLegacy;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageFilterSlot extends MessageHandlerPlayerToServer<MessageFilterSlot> implements IMessage {
    private int containerSlot;
    private ItemStack stack;

    public MessageFilterSlot(int containerSlot, ItemStack stack) {
        this.containerSlot = containerSlot;
        this.stack = stack;
    }

    public MessageFilterSlot() {
        // NO OP
    }

    @Override
    protected void handle(MessageFilterSlot message, EntityPlayerMP player) {
        Container container = player.openContainer;

        if (container != null) {
            if (message.containerSlot >= 0 && message.containerSlot < container.inventorySlots.size()) {
                Slot slot = container.getSlot(message.containerSlot);

                if (slot instanceof SlotFilter || slot instanceof SlotFilterLegacy) {
                    slot.putStack(message.stack);
                }
            }
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        containerSlot = buf.readInt();
        stack = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(containerSlot);
        ByteBufUtils.writeItemStack(buf, stack);
    }
}
