package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItem;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemHandler;
import com.raoulvdberge.refinedstorage.apiimpl.network.item.NetworkItemWirelessGrid;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeGrid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemWirelessGrid extends ItemNetworkItem {
    public ItemWirelessGrid(String name) {
        super(name, RS.INSTANCE.config.wirelessGridCapacity);
    }

    public ItemWirelessGrid() {
        super("wireless_grid", RS.INSTANCE.config.wirelessGridCapacity);
    }

    @Override
    @Nonnull
    public INetworkItem provide(INetworkItemHandler handler, EntityPlayer player, ItemStack stack) {
        return new NetworkItemWirelessGrid(handler, player, stack);
    }

    public static int getViewType(ItemStack stack) {
        return (stack.hasTagCompound() && stack.getTagCompound().hasKey(NetworkNodeGrid.NBT_VIEW_TYPE)) ? stack.getTagCompound().getInteger(NetworkNodeGrid.NBT_VIEW_TYPE) : IGrid.VIEW_TYPE_NORMAL;
    }

    public static int getSortingType(ItemStack stack) {
        return (stack.hasTagCompound() && stack.getTagCompound().hasKey(NetworkNodeGrid.NBT_SORTING_TYPE)) ? stack.getTagCompound().getInteger(NetworkNodeGrid.NBT_SORTING_TYPE) : IGrid.SORTING_TYPE_QUANTITY;
    }

    public static int getSortingDirection(ItemStack stack) {
        return (stack.hasTagCompound() && stack.getTagCompound().hasKey(NetworkNodeGrid.NBT_SORTING_DIRECTION)) ? stack.getTagCompound().getInteger(NetworkNodeGrid.NBT_SORTING_DIRECTION) : IGrid.SORTING_DIRECTION_DESCENDING;
    }

    public static int getSearchBoxMode(ItemStack stack) {
        return (stack.hasTagCompound() && stack.getTagCompound().hasKey(NetworkNodeGrid.NBT_SEARCH_BOX_MODE)) ? stack.getTagCompound().getInteger(NetworkNodeGrid.NBT_SEARCH_BOX_MODE) : IGrid.SEARCH_BOX_MODE_NORMAL;
    }

    public static int getTabSelected(ItemStack stack) {
        return (stack.hasTagCompound() && stack.getTagCompound().hasKey(NetworkNodeGrid.NBT_TAB_SELECTED)) ? stack.getTagCompound().getInteger(NetworkNodeGrid.NBT_TAB_SELECTED) : -1;
    }

    public static int getTabPage(ItemStack stack) {
        return (stack.hasTagCompound() && stack.getTagCompound().hasKey(NetworkNodeGrid.NBT_TAB_PAGE)) ? stack.getTagCompound().getInteger(NetworkNodeGrid.NBT_TAB_PAGE) : 0;
    }

    public static int getSize(ItemStack stack) {
        return (stack.hasTagCompound() && stack.getTagCompound().hasKey(NetworkNodeGrid.NBT_SIZE)) ? stack.getTagCompound().getInteger(NetworkNodeGrid.NBT_SIZE) : IGrid.SIZE_STRETCH;
    }
}
