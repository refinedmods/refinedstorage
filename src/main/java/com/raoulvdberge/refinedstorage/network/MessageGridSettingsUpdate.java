package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeGrid;
import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import com.raoulvdberge.refinedstorage.tile.grid.IGrid;
import com.raoulvdberge.refinedstorage.tile.grid.PortableGrid;
import com.raoulvdberge.refinedstorage.tile.grid.WirelessGrid;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageGridSettingsUpdate extends MessageHandlerPlayerToServer<MessageGridSettingsUpdate> implements IMessage {
    private int viewType;
    private int sortingDirection;
    private int sortingType;
    private int searchBoxMode;
    private int size;
    private int tabSelected;

    public MessageGridSettingsUpdate() {
    }

    public MessageGridSettingsUpdate(int viewType, int sortingDirection, int sortingType, int searchBoxMode, int size, int tabSelected) {
        this.viewType = viewType;
        this.sortingDirection = sortingDirection;
        this.sortingType = sortingType;
        this.searchBoxMode = searchBoxMode;
        this.size = size;
        this.tabSelected = tabSelected;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        viewType = buf.readInt();
        sortingDirection = buf.readInt();
        sortingType = buf.readInt();
        searchBoxMode = buf.readInt();
        size = buf.readInt();
        tabSelected = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(viewType);
        buf.writeInt(sortingDirection);
        buf.writeInt(sortingType);
        buf.writeInt(searchBoxMode);
        buf.writeInt(size);
        buf.writeInt(tabSelected);
    }

    @Override
    public void handle(MessageGridSettingsUpdate message, EntityPlayerMP player) {
        if (player.openContainer instanceof ContainerGrid) {
            IGrid grid = ((ContainerGrid) player.openContainer).getGrid();

            if (grid instanceof WirelessGrid || grid instanceof PortableGrid) {
                ItemStack stack = grid instanceof WirelessGrid ? ((WirelessGrid) grid).getStack() : ((PortableGrid) grid).getStack();

                if (!stack.hasTagCompound()) {
                    stack.setTagCompound(new NBTTagCompound());
                }

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

                if (NetworkNodeGrid.isValidSize(message.size)) {
                    stack.getTagCompound().setInteger(NetworkNodeGrid.NBT_SIZE, message.size);
                }

                stack.getTagCompound().setInteger(NetworkNodeGrid.NBT_TAB_SELECTED, message.tabSelected);
            }
        }
    }
}
