package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement;
import com.raoulvdberge.refinedstorage.apiimpl.API;
import com.raoulvdberge.refinedstorage.gui.GuiCraftingMonitor;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class MessageCraftingMonitorElements implements IMessage, IMessageHandler<MessageCraftingMonitorElements, IMessage> {
    private List<ICraftingMonitorElement> elements = new ArrayList<>();

    public MessageCraftingMonitorElements() {
    }

    public MessageCraftingMonitorElements(List<ICraftingMonitorElement> elements) {
        this.elements = elements;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();

        for (int i = 0; i < size; ++i) {
            Function<ByteBuf, ICraftingMonitorElement> factory = API.instance().getCraftingMonitorElementRegistry().get(ByteBufUtils.readUTF8String(buf));

            if (factory != null) {
                elements.add(factory.apply(buf));
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(elements.size());

        for (ICraftingMonitorElement task : elements) {
            ByteBufUtils.writeUTF8String(buf, task.getId());

            task.write(buf);
        }
    }

    @Override
    public IMessage onMessage(MessageCraftingMonitorElements message, MessageContext ctx) {
        GuiCraftingMonitor.ELEMENTS = message.elements;

        return null;
    }
}
