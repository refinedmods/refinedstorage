package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.api.network.item.INetworkItem;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemHandler;
import com.raoulvdberge.refinedstorage.apiimpl.network.item.NetworkItemWirelessGrid;
import com.raoulvdberge.refinedstorage.tile.grid.TileGrid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemWirelessGrid extends ItemNetworkItem {
    public ItemWirelessGrid() {
        super("wireless_grid");
    }

    public static int getViewType(ItemStack stack) {
        return stack.getTagCompound().getInteger(TileGrid.NBT_VIEW_TYPE);
    }

    public static int getSortingType(ItemStack stack) {
        return stack.getTagCompound().getInteger(TileGrid.NBT_SORTING_TYPE);
    }

    public static int getSortingDirection(ItemStack stack) {
        return stack.getTagCompound().getInteger(TileGrid.NBT_SORTING_DIRECTION);
    }

    public static int getSearchBoxMode(ItemStack stack) {
        return stack.getTagCompound().getInteger(TileGrid.NBT_SEARCH_BOX_MODE);
    }

    public boolean isValid(ItemStack stack) {
        return super.isValid(stack)
            && stack.getTagCompound().hasKey(TileGrid.NBT_VIEW_TYPE)
            && stack.getTagCompound().hasKey(TileGrid.NBT_SORTING_DIRECTION)
            && stack.getTagCompound().hasKey(TileGrid.NBT_SORTING_TYPE)
            && stack.getTagCompound().hasKey(TileGrid.NBT_SEARCH_BOX_MODE);
    }

    @Override
    public INetworkItem provide(INetworkItemHandler handler, EntityPlayer player, ItemStack stack) {
        return new NetworkItemWirelessGrid(handler, player, stack);
    }
}
