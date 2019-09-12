package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.gui.GuiBase;
import com.raoulvdberge.refinedstorage.gui.GuiReaderWriter;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MessageReaderWriterUpdate implements IMessage, IMessageHandler<MessageReaderWriterUpdate, IMessage> {
    private List<String> channels = new ArrayList<>();

    public MessageReaderWriterUpdate() {
    }

    public MessageReaderWriterUpdate(Collection<String> channels) {
        this.channels = new ArrayList<>(channels);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();

        for (int i = 0; i < size; ++i) {
            channels.add(ByteBufUtils.readUTF8String(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(channels.size());

        for (String channel : channels) {
            ByteBufUtils.writeUTF8String(buf, channel);
        }
    }

    @Override
    public IMessage onMessage(MessageReaderWriterUpdate message, MessageContext ctx) {
        GuiBase.executeLater(GuiReaderWriter.class, readerWriter -> readerWriter.setChannels(message.channels));

        return null;
    }
}
