package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import refinedstorage.storage.ItemGroup;
import refinedstorage.tile.grid.WirelessGrid;

import java.util.ArrayList;
import java.util.List;

public class MessageWirelessGridItems implements IMessage, IMessageHandler<MessageWirelessGridItems, IMessage> {
    private List<ItemGroup> itemGroups;

    public MessageWirelessGridItems() {
    }

    public MessageWirelessGridItems(List<ItemGroup> itemGroups) {
        this.itemGroups = itemGroups;
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
        buf.writeInt(itemGroups.size());

        for (ItemGroup group : itemGroups) {
            group.toBytes(buf, itemGroups.indexOf(group));
        }
    }

    @Override
    public IMessage onMessage(MessageWirelessGridItems message, MessageContext ctx) {
        return null;
    }
}
