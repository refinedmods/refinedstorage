package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.api.network.IGridHandler;
import refinedstorage.container.ContainerGrid;

public class MessageGridPull extends MessageHandlerPlayerToServer<MessageGridPull> implements IMessage {
    private int id;
    private int flags;

    public MessageGridPull() {
    }

    public MessageGridPull(int id, int flags) {
        this.id = id;
        this.flags = flags;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        id = buf.readInt();
        flags = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(id);
        buf.writeInt(flags);
    }

    @Override
    public void handle(MessageGridPull message, EntityPlayerMP player) {
        Container container = player.openContainer;

        if (container instanceof ContainerGrid) {
            IGridHandler handler = ((ContainerGrid) container).getGrid().getGridHandler();

            if (handler != null) {
                handler.onExtract(message.id, message.flags, player);
            }
        }
    }
}
