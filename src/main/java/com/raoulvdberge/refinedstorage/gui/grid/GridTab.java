package com.raoulvdberge.refinedstorage.gui.grid;

import net.minecraft.item.ItemStack;

import java.util.List;

public class GridTab {
    private List<GridFilteredItem> filters;
    private String name;
    private ItemStack icon;

    public GridTab(List<GridFilteredItem> filters, String name, ItemStack icon) {
        this.filters = filters;
        this.name = name;
        this.icon = icon;
    }

    public List<GridFilteredItem> getFilters() {
        return filters;
    }

    public String getName() {
        return name;
    }

    public ItemStack getIcon() {
        return icon;
    }
}
