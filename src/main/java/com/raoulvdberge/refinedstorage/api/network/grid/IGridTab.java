package com.raoulvdberge.refinedstorage.api.network.grid;

import com.raoulvdberge.refinedstorage.api.util.IFilter;
import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Represents a grid tab.
 */
public interface IGridTab {
    /**
     * @return the filters
     */
    List<IFilter> getFilters();

    /**
     * @return the name
     */
    String getName();

    /**
     * @return the icon
     */
    ItemStack getIcon();
}
