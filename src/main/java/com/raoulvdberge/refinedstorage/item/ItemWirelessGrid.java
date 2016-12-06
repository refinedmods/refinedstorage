package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.api.network.item.INetworkItem;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemHandler;
import com.raoulvdberge.refinedstorage.apiimpl.network.item.NetworkItemWirelessGrid;
import com.raoulvdberge.refinedstorage.tile.grid.TileGrid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemWirelessGrid extends ItemNetworkItem {
    public ItemWirelessGrid() {
        super("wireless_grid");
    }

    @Override
    public void initializeDefaults(NBTTagCompound tag) {
        super.initializeDefaults(tag);

        tag.setInteger(TileGrid.NBT_VIEW_TYPE, TileGrid.VIEW_TYPE_NORMAL);
        tag.setInteger(TileGrid.NBT_SORTING_DIRECTION, TileGrid.SORTING_DIRECTION_DESCENDING);
        tag.setInteger(TileGrid.NBT_SORTING_TYPE, TileGrid.SORTING_TYPE_QUANTITY);
        tag.setInteger(TileGrid.NBT_SEARCH_BOX_MODE, TileGrid.SEARCH_BOX_MODE_NORMAL);
    }

    @Override
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
}
