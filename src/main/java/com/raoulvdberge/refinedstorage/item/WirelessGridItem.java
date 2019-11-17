package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.RS;
import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItem;
import com.raoulvdberge.refinedstorage.api.network.item.INetworkItemManager;
import com.raoulvdberge.refinedstorage.apiimpl.network.item.WirelessGridNetworkItem;
import com.raoulvdberge.refinedstorage.apiimpl.network.node.GridNetworkNode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class WirelessGridItem extends NetworkItem {
    public enum Type {
        NORMAL,
        CREATIVE
    }

    private final Type type;

    public WirelessGridItem(Type type) {
        super(new Item.Properties().group(RS.MAIN_GROUP).maxStackSize(1), type == Type.CREATIVE, () -> RS.SERVER_CONFIG.getWirelessGrid().getCapacity());

        this.type = type;

        this.setRegistryName(RS.ID, (type == Type.CREATIVE ? "creative_" : "") + "wireless_grid");
    }

    public Type getType() {
        return type;
    }

    @Override
    @Nonnull
    public INetworkItem provide(INetworkItemManager handler, PlayerEntity player, ItemStack stack, int slotId) {
        return new WirelessGridNetworkItem(handler, player, stack, slotId);
    }

    public static int getViewType(ItemStack stack) {
        return (stack.hasTag() && stack.getTag().contains(GridNetworkNode.NBT_VIEW_TYPE)) ? stack.getTag().getInt(GridNetworkNode.NBT_VIEW_TYPE) : IGrid.VIEW_TYPE_NORMAL;
    }

    public static int getSortingType(ItemStack stack) {
        return (stack.hasTag() && stack.getTag().contains(GridNetworkNode.NBT_SORTING_TYPE)) ? stack.getTag().getInt(GridNetworkNode.NBT_SORTING_TYPE) : IGrid.SORTING_TYPE_QUANTITY;
    }

    public static int getSortingDirection(ItemStack stack) {
        return (stack.hasTag() && stack.getTag().contains(GridNetworkNode.NBT_SORTING_DIRECTION)) ? stack.getTag().getInt(GridNetworkNode.NBT_SORTING_DIRECTION) : IGrid.SORTING_DIRECTION_DESCENDING;
    }

    public static int getSearchBoxMode(ItemStack stack) {
        return (stack.hasTag() && stack.getTag().contains(GridNetworkNode.NBT_SEARCH_BOX_MODE)) ? stack.getTag().getInt(GridNetworkNode.NBT_SEARCH_BOX_MODE) : IGrid.SEARCH_BOX_MODE_NORMAL;
    }

    public static int getTabSelected(ItemStack stack) {
        return (stack.hasTag() && stack.getTag().contains(GridNetworkNode.NBT_TAB_SELECTED)) ? stack.getTag().getInt(GridNetworkNode.NBT_TAB_SELECTED) : -1;
    }

    public static int getTabPage(ItemStack stack) {
        return (stack.hasTag() && stack.getTag().contains(GridNetworkNode.NBT_TAB_PAGE)) ? stack.getTag().getInt(GridNetworkNode.NBT_TAB_PAGE) : 0;
    }

    public static int getSize(ItemStack stack) {
        return (stack.hasTag() && stack.getTag().contains(GridNetworkNode.NBT_SIZE)) ? stack.getTag().getInt(GridNetworkNode.NBT_SIZE) : IGrid.SIZE_STRETCH;
    }
}
