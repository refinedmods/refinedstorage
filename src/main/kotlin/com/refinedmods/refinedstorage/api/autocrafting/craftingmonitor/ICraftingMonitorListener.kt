package com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingManager

/**
 * Add this listener to a [ICraftingManager] to listen to crafting task changes.
 */
interface ICraftingMonitorListener {
    /**
     * Called when this listener is attached to a [ICraftingManager].
     */
    fun onAttached()

    /**
     * Called when any task changes.
     */
    fun onChanged()
}