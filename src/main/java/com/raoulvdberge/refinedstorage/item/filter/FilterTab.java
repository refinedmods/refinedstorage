package com.raoulvdberge.refinedstorage.item.filter;

import net.minecraft.item.ItemStack;

import java.util.List;

public class FilterTab {
    private List<Filter> filters;
    private String name;
    private ItemStack icon;

    public FilterTab(List<Filter> filters, String name, ItemStack icon) {
        this.filters = filters;
        this.name = name;
        this.icon = icon;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public String getName() {
        return name;
    }

    public ItemStack getIcon() {
        return icon;
    }
}
