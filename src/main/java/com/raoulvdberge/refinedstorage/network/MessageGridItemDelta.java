package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.gui.grid.stack.GridStackItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.function.Consumer;

public class MessageGridItemDelta implements IMessage, IMessageHandler<MessageGridItemDelta, IMessage> {
    // @todo: we can remove sendHandler if we improve the network == null condition in RSUtils.writeItemStack
    private Consumer<ByteBuf> sendHandler;
    private int delta;

    private GridStackItem clientStack;

    public MessageGridItemDelta() {
    }

    public MessageGridItemDelta(INetworkMaster network, ItemStack stack, int delta) {
        this.sendHandler = buf -> RSUtils.writeItemStack(buf, stack, network, false);
        this.delta = delta;
    }

    public MessageGridItemDelta(Consumer<ByteBuf> sendHandler, int delta) {
        this.sendHandler = sendHandler;
        this.delta = delta;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        clientStack = new GridStackItem(buf);
        delta = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        sendHandler.accept(buf);
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
