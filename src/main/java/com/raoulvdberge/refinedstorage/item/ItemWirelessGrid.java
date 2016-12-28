package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.api.network.item.INetworkItem;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemHandler;
import com.raoulvdberge.refinedstorage.apiimpl.network.item.NetworkItemWirelessGrid;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.NetworkNodeGrid;
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

        tag.setInteger(NetworkNodeGrid.NBT_VIEW_TYPE, NetworkNodeGrid.VIEW_TYPE_NORMAL);
        tag.setInteger(NetworkNodeGrid.NBT_SORTING_DIRECTION, NetworkNodeGrid.SORTING_DIRECTION_DESCENDING);
        tag.setInteger(NetworkNodeGrid.NBT_SORTING_TYPE, NetworkNodeGrid.SORTING_TYPE_QUANTITY);
        tag.setInteger(NetworkNodeGrid.NBT_SEARCH_BOX_MODE, NetworkNodeGrid.SEARCH_BOX_MODE_NORMAL);
        tag.setInteger(NetworkNodeGrid.NBT_SIZE, NetworkNodeGrid.SIZE_STRETCH);
        tag.setInteger(NetworkNodeGrid.NBT_TAB_SELECTED, -1);
    }

    @Override
    public boolean isValid(ItemStack stack) {
        return super.isValid(stack)
                && stack.getTagCompound().hasKey(NetworkNodeGrid.NBT_VIEW_TYPE)
                && stack.getTagCompound().hasKey(NetworkNodeGrid.NBT_SORTING_DIRECTION)
                && stack.getTagCompound().hasKey(NetworkNodeGrid.NBT_SORTING_TYPE)
                && stack.getTagCompound().hasKey(NetworkNodeGrid.NBT_SEARCH_BOX_MODE);
    }

    @Override
    public INetworkItem provide(INetworkItemHandler handler, EntityPlayer player, ItemStack stack) {
        return new NetworkItemWirelessGrid(handler, player, stack);
    }

    public static int getViewType(ItemStack stack) {
        return stack.getTagCompound().getInteger(NetworkNodeGrid.NBT_VIEW_TYPE);
    }

    public static int getSortingType(ItemStack stack) {
        return stack.getTagCompound().getInteger(NetworkNodeGrid.NBT_SORTING_TYPE);
    }

    public static int getSortingDirection(ItemStack stack) {
        return stack.getTagCompound().getInteger(NetworkNodeGrid.NBT_SORTING_DIRECTION);
    }

    public static int getSearchBoxMode(ItemStack stack) {
        return stack.getTagCompound().getInteger(NetworkNodeGrid.NBT_SEARCH_BOX_MODE);
    }

    public static int getTabSelected(ItemStack stack) {
        return (stack.hasTagCompound() && stack.getTagCompound().hasKey(NetworkNodeGrid.NBT_TAB_SELECTED)) ? stack.getTagCompound().getInteger(NetworkNodeGrid.NBT_TAB_SELECTED) : -1;
    }

    public static int getSize(ItemStack stack) {
        return (stack.hasTagCompound() && stack.getTagCompound().hasKey(NetworkNodeGrid.NBT_SIZE)) ? stack.getTagCompound().getInteger(NetworkNodeGrid.NBT_SIZE) : NetworkNodeGrid.SIZE_STRETCH;
    }
}
