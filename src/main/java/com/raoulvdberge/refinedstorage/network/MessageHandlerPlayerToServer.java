package com.raoulvdberge.refinedstorage.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class MessageHandlerPlayerToServer<T extends IMessage> implements IMessageHandler<T, IMessage> {
    @Override
    public IMessage onMessage(final T message, MessageContext context) {
        final ServerPlayerEntity player = context.getServerHandler().player;

        player.getServerWorld().addScheduledTask(() -> handle(message, player));

        return null;
    }

    protected abstract void handle(T message, ServerPlayerEntity player);
}
