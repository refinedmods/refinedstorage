package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeGrid;
import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import com.raoulvdberge.refinedstorage.tile.grid.IGrid;
import com.raoulvdberge.refinedstorage.tile.grid.WirelessGrid;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageWirelessGridSettingsUpdate extends MessageHandlerPlayerToServer<MessageWirelessGridSettingsUpdate> implements IMessage {
    private int viewType;
    private int sortingDirection;
    private int sortingType;
    private int searchBoxMode;
    private int tabSelected;

    public MessageWirelessGridSettingsUpdate() {
    }

    public MessageWirelessGridSettingsUpdate(int viewType, int sortingDirection, int sortingType, int searchBoxMode, int tabSelected) {
        this.viewType = viewType;
        this.sortingDirection = sortingDirection;
        this.sortingType = sortingType;
        this.searchBoxMode = searchBoxMode;
        this.tabSelected = tabSelected;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        viewType = buf.readInt();
        sortingDirection = buf.readInt();
        sortingType = buf.readInt();
        searchBoxMode = buf.readInt();
        tabSelected = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(viewType);
        buf.writeInt(sortingDirection);
        buf.writeInt(sortingType);
        buf.writeInt(searchBoxMode);
        buf.writeInt(tabSelected);
    }

    @Override
    public void handle(MessageWirelessGridSettingsUpdate message, EntityPlayerMP player) {
        if (player.openContainer instanceof ContainerGrid) {
            IGrid grid = ((ContainerGrid) player.openContainer).getGrid();

            if (grid instanceof WirelessGrid) {
                ItemStack stack = ((WirelessGrid) grid).getStack();

                if (NetworkNodeGrid.isValidViewType(message.viewType)) {
                    stack.getTagCompound().setInteger(NetworkNodeGrid.NBT_VIEW_TYPE, message.viewType);
                }

                if (NetworkNodeGrid.isValidSortingDirection(message.sortingDirection)) {
                    stack.getTagCompound().setInteger(NetworkNodeGrid.NBT_SORTING_DIRECTION, message.sortingDirection);
                }

                if (NetworkNodeGrid.isValidSortingType(message.sortingType)) {
                    stack.getTagCompound().setInteger(NetworkNodeGrid.NBT_SORTING_TYPE, message.sortingType);
                }

                if (NetworkNodeGrid.isValidSearchBoxMode(message.searchBoxMode)) {
                    stack.getTagCompound().setInteger(NetworkNodeGrid.NBT_SEARCH_BOX_MODE, message.searchBoxMode);
                }

                stack.getTagCompound().setInteger(NetworkNodeGrid.NBT_TAB_SELECTED, message.tabSelected);
            }
        }
    }
}
