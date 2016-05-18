package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import refinedstorage.storage.ItemGroup;
import refinedstorage.tile.controller.TileController;
import refinedstorage.tile.grid.WirelessGrid;

import java.util.ArrayList;
import java.util.List;

public class MessageWirelessGridItems implements IMessage, IMessageHandler<MessageWirelessGridItems, IMessage> {
    private TileController controller;

    public MessageWirelessGridItems() {
    }

    public MessageWirelessGridItems(TileController controller) {
        this.controller = controller;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int size = buf.readInt();

        List<ItemGroup> groups = new ArrayList<ItemGroup>();

        for (int i = 0; i < size; ++i) {
            groups.add(new ItemGroup(buf));
        }

        WirelessGrid.ITEM_GROUPS = groups;
        WirelessGrid.LAST_ITEM_GROUP_UPDATE = System.currentTimeMillis();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        controller.sendItemGroups(buf);
    }

    @Override
    public IMessage onMessage(MessageWirelessGridItems message, MessageContext ctx) {
        return null;
    }
}
