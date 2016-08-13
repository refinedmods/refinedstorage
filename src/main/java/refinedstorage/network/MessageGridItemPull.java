package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.api.network.grid.IItemGridHandler;
import refinedstorage.container.ContainerGrid;

public class MessageGridItemPull extends MessageHandlerPlayerToServer<MessageGridItemPull> implements IMessage {
    private int hash;
    private int flags;

    public MessageGridItemPull() {
    }

    public MessageGridItemPull(int hash, int flags) {
        this.hash = hash;
        this.flags = flags;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        hash = buf.readInt();
        flags = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(hash);
        buf.writeInt(flags);
    }

    @Override
    public void handle(MessageGridItemPull message, EntityPlayerMP player) {
        Container container = player.openContainer;

        if (container instanceof ContainerGrid) {
            IItemGridHandler handler = ((ContainerGrid) container).getGrid().getItemHandler();

            if (handler != null) {
                handler.onExtract(message.hash, message.flags, player);
            }
        }
    }
}
