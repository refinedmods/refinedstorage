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
import refinedstorage.storage.ClientItemGroup;
import refinedstorage.storage.ItemGroup;
import refinedstorage.tile.TileController;

import java.util.ArrayList;
import java.util.List;

public class MessageGridItems implements IMessage, IMessageHandler<MessageGridItems, IMessage> {
    private TileController controller;
    private List<ClientItemGroup> groups = new ArrayList<ClientItemGroup>();

    public MessageGridItems() {
    }

    public MessageGridItems(TileController controller) {
        this.controller = controller;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();

        for (int i = 0; i < size; ++i) {
            int id = buf.readInt();
            int quantity = buf.readInt();
            ItemStack stack = ByteBufUtils.readItemStack(buf);
            stack.stackSize = quantity;
            groups.add(new ClientItemGroup(id, stack));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(controller.getItemGroups().size());

        for (int i = 0; i < controller.getItemGroups().size(); ++i) {
            buf.writeInt(i);
            buf.writeInt(controller.getItemGroups().get(i).getQuantity());
            ByteBufUtils.writeItemStack(buf, controller.getItemGroups().get(i).toStack());
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
