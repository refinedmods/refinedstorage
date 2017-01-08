
package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.proxy.ProxyClient;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageGridCraftingStartResponse implements IMessage, IMessageHandler<MessageGridCraftingStartResponse, IMessage> {
    public MessageGridCraftingStartResponse() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        // NO OP
    }

    @Override
    public void toBytes(ByteBuf buf) {
        // NO OP
    }

    @Override
    public IMessage onMessage(MessageGridCraftingStartResponse message, MessageContext ctx) {
        ProxyClient.onReceiveCraftingStartResponse();

        return null;
    }
}