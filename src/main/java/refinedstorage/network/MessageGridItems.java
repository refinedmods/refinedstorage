package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import refinedstorage.api.storagenet.NetworkMaster;
import refinedstorage.container.ContainerGrid;
import refinedstorage.tile.ClientItem;

import java.util.ArrayList;
import java.util.List;

public class MessageGridItems implements IMessage, IMessageHandler<MessageGridItems, IMessage> {
    private NetworkMaster network;
    private List<ClientItem> items = new ArrayList<ClientItem>();

    public MessageGridItems() {
    }

    public MessageGridItems(NetworkMaster network) {
        this.network = network;
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
        buf.writeInt(network.getItems().size());

        for (ItemStack item : network.getItems()) {
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
