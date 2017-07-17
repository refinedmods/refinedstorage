package com.raoulvdberge.refinedstorage.api.network.grid;

import com.raoulvdberge.refinedstorage.api.util.IFilter;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface IGridTab {
    List<IFilter> getFilters();

    String getName();

    ItemStack getIcon();
}
