package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import refinedstorage.container.ContainerGrid;
import refinedstorage.storage.ItemGroup;
import refinedstorage.tile.controller.TileController;

import java.util.ArrayList;
import java.util.List;

public class MessageGridItems implements IMessage, IMessageHandler<MessageGridItems, IMessage> {
    private TileController controller;
    private List<ItemGroup> groups = new ArrayList<ItemGroup>();

    public MessageGridItems() {
    }

    public MessageGridItems(TileController controller) {
        this.controller = controller;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();

        for (int i = 0; i < size; ++i) {
            groups.add(new ItemGroup(buf));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(controller.getItemGroups().size());

        for (int i = 0; i < controller.getItemGroups().size(); ++i) {
            controller.getItemGroups().get(i).toBytes(buf, i);
        }
    }

    @Override
    public IMessage onMessage(MessageGridItems message, MessageContext ctx) {
        Container container = Minecraft.getMinecraft().thePlayer.openContainer;

        if (container instanceof ContainerGrid) {
            ((ContainerGrid) container).getGrid().setItemGroups(message.groups);
        }

        return null;
    }
}
