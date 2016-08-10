package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.api.network.grid.IItemGridHandler;
import refinedstorage.container.ContainerGrid;

public class MessageGridCraftingStart extends MessageHandlerPlayerToServer<MessageGridCraftingStart> implements IMessage {
    private int id;
    private int quantity;

    public MessageGridCraftingStart() {
    }

    public MessageGridCraftingStart(int id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        id = buf.readInt();
        quantity = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(id);
        buf.writeInt(quantity);
    }

    @Override
    public void handle(MessageGridCraftingStart message, EntityPlayerMP player) {
        Container container = player.openContainer;

        if (container instanceof ContainerGrid) {
            IItemGridHandler handler = ((ContainerGrid) container).getGrid().getHandler();

            if (handler != null) {
                handler.onCraftingRequested(message.id, message.quantity);
            }
        }
    }
}
