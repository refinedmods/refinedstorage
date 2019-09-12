package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.container.ContainerReaderWriter;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageReaderWriterChannelRemove extends MessageHandlerPlayerToServer<MessageReaderWriterChannelRemove> implements IMessage {
    private String name;

    public MessageReaderWriterChannelRemove() {
    }

    public MessageReaderWriterChannelRemove(String name) {
        this.name = name;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        name = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, name);
    }

    @Override
    public void handle(MessageReaderWriterChannelRemove message, ServerPlayerEntity player) {
        if (player.openContainer instanceof ContainerReaderWriter) {
            ((ContainerReaderWriter) player.openContainer).getReaderWriter().onRemove(message.name);
        }
    }
}
