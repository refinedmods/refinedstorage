package com.raoulvdberge.refinedstorage.api.autocrafting.craftingmonitor;

import com.raoulvdberge.refinedstorage.api.autocrafting.ICraftingManager;

/**
 * Add this listener to a {@link ICraftingManager} to listen to crafting task changes.
 */
public interface ICraftingMonitorListener {
    /**
     * Called when this listener is attached to a {@link ICraftingManager}.
     */
    void onAttached();

    /**
     * Called when any task changes.
     */
    void onChanged();
}
