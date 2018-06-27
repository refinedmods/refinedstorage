package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.RSConfig;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageConfigSync implements IMessage, IMessageHandler<MessageConfigSync, IMessage> {
    @Override
    public void fromBytes(ByteBuf buf) {
        RSConfig serverVersion = new RSConfig(RS.INSTANCE.config, RS.INSTANCE.config.getConfig());

        serverVersion.controllerCapacity = buf.readInt();

        RS.INSTANCE.config = serverVersion;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(RS.INSTANCE.config.controllerCapacity);
    }

    @Override
    public IMessage onMessage(MessageConfigSync message, MessageContext ctx) {
        return null;
    }
}
