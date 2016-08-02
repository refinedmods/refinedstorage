package refinedstorage.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import refinedstorage.container.ContainerGrid;
import refinedstorage.tile.grid.IGrid;
import refinedstorage.tile.grid.TileGrid;
import refinedstorage.tile.grid.WirelessGrid;

public class MessageWirelessGridSettingsUpdate extends MessageHandlerPlayerToServer<MessageWirelessGridSettingsUpdate> implements IMessage {
    private int viewType;
    private int sortingDirection;
    private int sortingType;
    private int searchBoxMode;

    public MessageWirelessGridSettingsUpdate() {
    }

    public MessageWirelessGridSettingsUpdate(int viewType, int sortingDirection, int sortingType, int searchBoxMode) {
        this.viewType = viewType;
        this.sortingDirection = sortingDirection;
        this.sortingType = sortingType;
        this.searchBoxMode = searchBoxMode;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        viewType = buf.readInt();
        sortingDirection = buf.readInt();
        sortingType = buf.readInt();
        searchBoxMode = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(viewType);
        buf.writeInt(sortingDirection);
        buf.writeInt(sortingType);
        buf.writeInt(searchBoxMode);
    }

    @Override
    public void handle(MessageWirelessGridSettingsUpdate message, EntityPlayerMP player) {
        if (player.openContainer instanceof ContainerGrid) {
            IGrid grid = ((ContainerGrid) player.openContainer).getGrid();

            if (grid instanceof WirelessGrid) {
                ItemStack stack = ((WirelessGrid) grid).getStack();

                if (TileGrid.isValidViewType(message.viewType)) {
                    stack.getTagCompound().setInteger(TileGrid.NBT_VIEW_TYPE, message.viewType);
                }

                if (TileGrid.isValidSortingDirection(message.sortingDirection)) {
                    stack.getTagCompound().setInteger(TileGrid.NBT_SORTING_DIRECTION, message.sortingDirection);
                }

                if (TileGrid.isValidSortingType(message.sortingType)) {
                    stack.getTagCompound().setInteger(TileGrid.NBT_SORTING_TYPE, message.sortingType);
                }

                if (TileGrid.isValidSearchBoxMode(message.searchBoxMode)) {
                    stack.getTagCompound().setInteger(TileGrid.NBT_SEARCH_BOX_MODE, message.searchBoxMode);
                }
            }
        }
    }
}
