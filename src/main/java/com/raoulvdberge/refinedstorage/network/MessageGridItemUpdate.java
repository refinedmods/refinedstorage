package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.RSUtils;
import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import com.raoulvdberge.refinedstorage.gui.grid.GuiGrid;
import com.raoulvdberge.refinedstorage.gui.grid.stack.GridStackItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.List;

public class MessageGridItemUpdate implements IMessage, IMessageHandler<MessageGridItemUpdate, IMessage> {
    private INetworkMaster network;
    private List<GridStackItem> stacks = new ArrayList<>();

    public MessageGridItemUpdate() {
    }

    public MessageGridItemUpdate(INetworkMaster network) {
        this.network = network;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int items = buf.readInt();

        for (int i = 0; i < items; ++i) {
            this.stacks.add(new GridStackItem(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(network.getItemStorageCache().getList().getStacks().size());

        for (ItemStack stack : network.getItemStorageCache().getList().getStacks()) {
            RSUtils.writeItemStack(buf, stack, network);
        }
    }

    @Override
    public IMessage onMessage(MessageGridItemUpdate message, MessageContext ctx) {
        GuiGrid.ITEMS.clear();

        for (GridStackItem item : message.stacks) {
            GuiGrid.ITEMS.put(item.getStack().getItem(), item);
        }

        GuiGrid.markForSorting();

        return null;
    }
}
