package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.INetwork;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.gui.grid.stack.GridStackItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import javax.annotation.Nullable;

public class MessageGridItemDelta implements IMessage, IMessageHandler<MessageGridItemDelta, IMessage> {
    @Nullable
    private INetwork network;
    private ItemStack stack;
    private int delta;

    private GridStackItem clientStack;

    public MessageGridItemDelta() {
    }

    public MessageGridItemDelta(@Nullable INetwork network, ItemStack stack, int delta) {
        this.network = network;
        this.stack = stack;
        this.delta = delta;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        clientStack = new GridStackItem(buf);
        delta = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        RSUtils.writeItemStack(buf, stack, network, false);
        buf.writeInt(delta);
    }

    @Override
    public IMessage onMessage(MessageGridItemDelta message, MessageContext ctx) {
        Item item = message.clientStack.getStack().getItem();

        for (GridStackItem stack : GuiGrid.ITEMS.get(item)) {
            if (stack.equals(message.clientStack)) {
                if (stack.getStack().getCount() + message.delta == 0) {
                    if (message.clientStack.isCraftable()) {
                        stack.setDisplayCraftText(true);
                    } else {
                        GuiGrid.ITEMS.remove(item, stack);
                    }
                } else {
                    if (stack.doesDisplayCraftText()) {
                        stack.setDisplayCraftText(false);

                        stack.getStack().setCount(message.delta);
                    } else {
                        stack.getStack().grow(message.delta);
                    }
                }

                GuiGrid.markForSorting();

                return null;
            }
        }

        message.clientStack.getStack().setCount(message.delta);

        GuiGrid.ITEMS.put(item, message.clientStack);
        GuiGrid.markForSorting();

        return null;
    }
}
