package com.raoulvdberge.refinedstorage.apiimpl.network.grid;

import com.raoulvdberge.refinedstorage.api.network.grid.IGridTab;
import com.raoulvdberge.refinedstorage.api.util.IFilter;
import net.minecraft.item.ItemStack;

import java.util.List;

public class GridTab implements IGridTab {
    private List<IFilter> filters;
    private String name;
    private ItemStack icon;

    public GridTab(List<IFilter> filters, String name, ItemStack icon) {
        this.filters = filters;
        this.name = name;
        this.icon = icon;
    }

    @Override
    public List<IFilter> getFilters() {
        return filters;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ItemStack getIcon() {
        return icon;
    }
}
