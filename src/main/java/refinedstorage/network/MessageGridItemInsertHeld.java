package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.api.network.grid.IItemGridHandler;
import refinedstorage.container.ContainerGrid;

public class MessageGridItemInsertHeld extends MessageHandlerPlayerToServer<MessageGridItemInsertHeld> implements IMessage {
    private boolean single;

    public MessageGridItemInsertHeld() {
    }

    public MessageGridItemInsertHeld(boolean single) {
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
    public void handle(MessageGridItemInsertHeld message, EntityPlayerMP player) {
        Container container = player.openContainer;

        if (container instanceof ContainerGrid) {
            IItemGridHandler handler = ((ContainerGrid) container).getGrid().getItemHandler();

            if (handler != null) {
                handler.onInsertHeldItem(player, message.single);
            }
        }
    }
}
