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

    public MessageWirelessFluidGridSettingsUpdate() {
    }

    public MessageWirelessFluidGridSettingsUpdate(int sortingDirection, int sortingType, int searchBoxMode, int size) {
        this.sortingDirection = sortingDirection;
        this.sortingType = sortingType;
        this.searchBoxMode = searchBoxMode;
        this.size = size;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        sortingDirection = buf.readInt();
        sortingType = buf.readInt();
        searchBoxMode = buf.readInt();
        size = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(sortingDirection);
        buf.writeInt(sortingType);
        buf.writeInt(searchBoxMode);
        buf.writeInt(size);
    }

    @Override
    public void handle(MessageWirelessFluidGridSettingsUpdate message, EntityPlayerMP player) {
        if (player.openContainer instanceof ContainerGrid) {
            IGrid grid = ((ContainerGrid) player.openContainer).getGrid();

            if (grid instanceof WirelessFluidGrid) {
                ItemStack stack = ((WirelessFluidGrid) grid).getStack();

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
            }
        }
    }
}
