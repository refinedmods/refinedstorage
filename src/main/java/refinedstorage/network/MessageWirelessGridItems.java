package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import refinedstorage.storage.ItemGroup;
import refinedstorage.tile.grid.WirelessGrid;

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

        WirelessGrid.ITEM_GROUPS.clear();

        WirelessGrid.LAST_ITEM_GROUP_UPDATE = System.currentTimeMillis();

        for (int i = 0; i < size; ++i) {
            WirelessGrid.ITEM_GROUPS.add(new ItemGroup(buf));
        }
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
