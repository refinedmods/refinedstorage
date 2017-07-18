package com.raoulvdberge.refinedstorage.api.util;

import net.minecraft.item.ItemStack;

/**
 * A filter.
 */
public interface IFilter {
    int MODE_WHITELIST = 0;
    int MODE_BLACKLIST = 1;

    /**
     * @return the stack being filtered
     */
    ItemStack getStack();

    /**
     * @return the compare flags, see {@link IComparer}
     */
    int getCompare();

    /**
     * @return the mode, whitelist or blacklist
     */
    int getMode();

    /**
     * @return true if this is a mod filter, false otherwise
     */
    boolean isModFilter();
}
