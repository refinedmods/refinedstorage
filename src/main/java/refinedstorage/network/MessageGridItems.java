package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import refinedstorage.container.ContainerGrid;
import refinedstorage.storage.ClientItem;
import refinedstorage.tile.controller.TileController;

import java.util.ArrayList;
import java.util.List;

public class MessageGridItems implements IMessage, IMessageHandler<MessageGridItems, IMessage> {
    private TileController controller;
    private List<ClientItem> items = new ArrayList<ClientItem>();

    public MessageGridItems() {
    }

    public MessageGridItems(TileController controller) {
        this.controller = controller;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int items = buf.readInt();

        for (int i = 0; i < items; ++i) {
            int size = buf.readInt();

            ItemStack stack = ByteBufUtils.readItemStack(buf);
            stack.stackSize = size;

            this.items.add(new ClientItem(i, stack));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(controller.getItems().size());

        for (ItemStack item : controller.getItems()) {
            buf.writeInt(item.stackSize);
            ByteBufUtils.writeItemStack(buf, item);
        }
    }

    @Override
    public IMessage onMessage(MessageGridItems message, MessageContext ctx) {
        Container container = Minecraft.getMinecraft().thePlayer.openContainer;

        if (container instanceof ContainerGrid) {
            ((ContainerGrid) container).getGrid().setItems(message.items);
        }

        return null;
    }
}
