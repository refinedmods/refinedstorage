package com.raoulvdberge.refinedstorage.network;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeGrid;
import com.raoulvdberge.refinedstorage.container.ContainerGrid;
import com.raoulvdberge.refinedstorage.tile.grid.WirelessGrid;
import com.raoulvdberge.refinedstorage.tile.grid.portable.PortableGrid;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class MessageGridSettingsUpdate extends MessageHandlerPlayerToServer<MessageGridSettingsUpdate> implements IMessage {
    private int viewType;
    private int sortingDirection;
    private int sortingType;
    private int searchBoxMode;
    private int size;
    private int tabSelected;
    private int tabPage;

    public MessageGridSettingsUpdate() {
    }

    public MessageGridSettingsUpdate(int viewType, int sortingDirection, int sortingType, int searchBoxMode, int size, int tabSelected, int tabPage) {
        this.viewType = viewType;
        this.sortingDirection = sortingDirection;
        this.sortingType = sortingType;
        this.searchBoxMode = searchBoxMode;
        this.size = size;
        this.tabSelected = tabSelected;
        this.tabPage = tabPage;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        viewType = buf.readInt();
        sortingDirection = buf.readInt();
        sortingType = buf.readInt();
        searchBoxMode = buf.readInt();
        size = buf.readInt();
        tabSelected = buf.readInt();
        tabPage = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(viewType);
        buf.writeInt(sortingDirection);
        buf.writeInt(sortingType);
        buf.writeInt(searchBoxMode);
        buf.writeInt(size);
        buf.writeInt(tabSelected);
        buf.writeInt(tabPage);
    }

    @Override
    public void handle(MessageGridSettingsUpdate message, ServerPlayerEntity player) {
        if (player.openContainer instanceof ContainerGrid) {
            IGrid grid = ((ContainerGrid) player.openContainer).getGrid();

            if (grid instanceof WirelessGrid || grid instanceof PortableGrid) {
                ItemStack stack = grid instanceof WirelessGrid ? ((WirelessGrid) grid).getStack() : ((PortableGrid) grid).getStack();

                if (!stack.hasTagCompound()) {
                    stack.setTagCompound(new CompoundNBT());
                }

                if (IGrid.isValidViewType(message.viewType)) {
                    stack.getTagCompound().putInt(NetworkNodeGrid.NBT_VIEW_TYPE, message.viewType);
                }

                if (IGrid.isValidSortingDirection(message.sortingDirection)) {
                    stack.getTagCompound().putInt(NetworkNodeGrid.NBT_SORTING_DIRECTION, message.sortingDirection);
                }

                if (IGrid.isValidSortingType(message.sortingType)) {
                    stack.getTagCompound().putInt(NetworkNodeGrid.NBT_SORTING_TYPE, message.sortingType);
                }

                if (IGrid.isValidSearchBoxMode(message.searchBoxMode)) {
                    stack.getTagCompound().putInt(NetworkNodeGrid.NBT_SEARCH_BOX_MODE, message.searchBoxMode);
                }

                if (IGrid.isValidSize(message.size)) {
                    stack.getTagCompound().putInt(NetworkNodeGrid.NBT_SIZE, message.size);
                }

                stack.getTagCompound().putInt(NetworkNodeGrid.NBT_TAB_SELECTED, message.tabSelected);
                stack.getTagCompound().putInt(NetworkNodeGrid.NBT_TAB_PAGE, message.tabPage);
            }
        }
    }
}
