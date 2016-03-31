package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.RefinedStorageItems;
import refinedstorage.item.ItemWirelessGrid;
import refinedstorage.tile.grid.TileGrid;

public class MessageWirelessGridSettingsUpdate extends MessageHandlerPlayerToServer<MessageWirelessGridSettingsUpdate> implements IMessage {
    private int hand;
    private int sortingDirection;
    private int sortingType;
    private int searchBoxMode;

    public MessageWirelessGridSettingsUpdate() {
    }

    public MessageWirelessGridSettingsUpdate(int hand, int sortingDirection, int sortingType, int searchBoxMode) {
        this.hand = hand;
        this.sortingDirection = sortingDirection;
        this.sortingType = sortingType;
        this.searchBoxMode = searchBoxMode;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        hand = buf.readInt();
        sortingDirection = buf.readInt();
        sortingType = buf.readInt();
        searchBoxMode = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(hand);
        buf.writeInt(sortingDirection);
        buf.writeInt(sortingType);
        buf.writeInt(searchBoxMode);
    }

    @Override
    public void handle(MessageWirelessGridSettingsUpdate message, EntityPlayerMP player) {
        ItemStack held = player.getHeldItem(hand == 1 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);

        if (held != null && held.getItem() == RefinedStorageItems.WIRELESS_GRID && held.getTagCompound() != null) {
            if (message.sortingDirection == TileGrid.SORTING_DIRECTION_ASCENDING || message.sortingDirection == TileGrid.SORTING_DIRECTION_DESCENDING) {
                held.getTagCompound().setInteger(ItemWirelessGrid.NBT_SORTING_DIRECTION, message.sortingDirection);
            }

            if (message.sortingType == TileGrid.SORTING_TYPE_QUANTITY || message.sortingType == TileGrid.SORTING_TYPE_NAME) {
                held.getTagCompound().setInteger(ItemWirelessGrid.NBT_SORTING_TYPE, message.sortingType);
            }

            if (message.searchBoxMode == TileGrid.SEARCH_BOX_MODE_NORMAL || message.searchBoxMode == TileGrid.SEARCH_BOX_MODE_JEI_SYNCHRONIZED) {
                held.getTagCompound().setInteger(ItemWirelessGrid.NBT_SEARCH_BOX_MODE, message.searchBoxMode);
            }
        }
    }
}
