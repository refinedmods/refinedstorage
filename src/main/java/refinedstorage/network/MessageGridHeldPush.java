package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.container.ContainerGrid;
import refinedstorage.tile.controller.StorageHandler;

public class MessageGridHeldPush extends MessageHandlerPlayerToServer<MessageGridHeldPush> implements IMessage {
    private boolean single;

    public MessageGridHeldPush() {
    }

    public MessageGridHeldPush(boolean single) {
        this.single = single;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        single = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(single);
    }

    @Override
    public void handle(MessageGridHeldPush message, EntityPlayerMP player) {
        Container container = player.openContainer;

        if (container instanceof ContainerGrid) {
            StorageHandler handler = ((ContainerGrid) container).getGrid().getStorageHandler();

            if (handler != null) {
                handler.onHeldItemPush(message.single, player);
            }
        }
    }
}
