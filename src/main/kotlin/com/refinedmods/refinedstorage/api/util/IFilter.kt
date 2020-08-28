package com.refinedmods.refinedstorage.api.util



/**
 * A filter.
 */
interface IFilter<T> {
    /**
     * @return the stack being filtered
     */
    val stack: T

    /**
     * @return the compare flags, see [IComparer]
     */
    val compare: Int

    /**
     * @return the mode, whitelist or blacklist
     */
    val mode: Int

    /**
     * @return true if this is a mod filter, false otherwise
     */
    val isModFilter: Boolean

    companion object {
        const val MODE_WHITELIST = 0
        const val MODE_BLACKLIST = 1
    }
}