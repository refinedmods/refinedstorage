package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeGrid;
import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import com.raoulvdberge.refinedstorage.tile.grid.WirelessFluidGrid;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageWirelessFluidGridSettingsUpdate extends MessageHandlerPlayerToServer<MessageWirelessFluidGridSettingsUpdate> implements IMessage {
    private int sortingDirection;
    private int sortingType;
    private int searchBoxMode;
    private int size;
    private int tabSelected;
    private int tabPage;

    public MessageWirelessFluidGridSettingsUpdate() {
    }

    public MessageWirelessFluidGridSettingsUpdate(int sortingDirection, int sortingType, int searchBoxMode, int size, int tabSelected, int tabPage) {
        this.sortingDirection = sortingDirection;
        this.sortingType = sortingType;
        this.searchBoxMode = searchBoxMode;
        this.size = size;
        this.tabSelected = tabSelected;
        this.tabPage = tabPage;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        sortingDirection = buf.readInt();
        sortingType = buf.readInt();
        searchBoxMode = buf.readInt();
        size = buf.readInt();
        tabSelected = buf.readInt();
        tabPage = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(sortingDirection);
        buf.writeInt(sortingType);
        buf.writeInt(searchBoxMode);
        buf.writeInt(size);
        buf.writeInt(tabSelected);
        buf.writeInt(tabPage);
    }

    @Override
    public void handle(MessageWirelessFluidGridSettingsUpdate message, EntityPlayerMP player) {
        if (player.openContainer instanceof ContainerGrid) {
            IGrid grid = ((ContainerGrid) player.openContainer).getGrid();

            if (grid instanceof WirelessFluidGrid) {
                ItemStack stack = ((WirelessFluidGrid) grid).getStack();

                if (IGrid.isValidSortingDirection(message.sortingDirection)) {
                    stack.getTagCompound().setInteger(NetworkNodeGrid.NBT_SORTING_DIRECTION, message.sortingDirection);
                }

                if (IGrid.isValidSortingType(message.sortingType)) {
                    stack.getTagCompound().setInteger(NetworkNodeGrid.NBT_SORTING_TYPE, message.sortingType);
                }

                if (IGrid.isValidSearchBoxMode(message.searchBoxMode)) {
                    stack.getTagCompound().setInteger(NetworkNodeGrid.NBT_SEARCH_BOX_MODE, message.searchBoxMode);
                }

                if (IGrid.isValidSize(message.size)) {
                    stack.getTagCompound().setInteger(NetworkNodeGrid.NBT_SIZE, message.size);
                }

                stack.getTagCompound().setInteger(NetworkNodeGrid.NBT_TAB_SELECTED, message.tabSelected);
                stack.getTagCompound().setInteger(NetworkNodeGrid.NBT_TAB_PAGE, message.tabPage);
            }
        }
    }
}
