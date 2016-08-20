package refinedstorage.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class MessageHandlerPlayerToServer<T extends IMessage> implements IMessageHandler<T, IMessage> {
    @Override
    public IMessage onMessage(final T message, MessageContext context) {
        final EntityPlayerMP player = context.getServerHandler().playerEntity;

        player.getServerWorld().addScheduledTask(() -> handle(message, player));

        return null;
    }

    public abstract void handle(T message, EntityPlayerMP player);
}
