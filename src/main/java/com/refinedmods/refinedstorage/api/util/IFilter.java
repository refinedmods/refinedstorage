package com.refinedmods.refinedstorage.api.util;

/**
 * A filter.
 */
public interface IFilter<T> {
    int MODE_WHITELIST = 0;
    int MODE_BLACKLIST = 1;

    /**
     * @return the stack being filtered
     */
    T getStack();

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
