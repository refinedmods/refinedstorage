package com.raoulvdberge.refinedstorage.api.network.item;

/**
 * Represents an network item action.
 * Called with {@link INetworkItem#onAction(NetworkItemAction)}.
 */
public enum NetworkItemAction {
    /**
     * Used when an item is inserted in a grid.
     */
    ITEM_INSERTED,
    /**
     * Used when an item is extracted in a grid.
     */
    ITEM_EXTRACTED,
    /**
     * Used when an item is crafted in a crafting grid (regular crafting, not autocrafting).
     */
    ITEM_CRAFTED,
    /**
     * Used when a fluid is inserted in a grid.
     */
    FLUID_INSERTED,
    /**
     * Used when a fluid is extracted in a grid.
     */
    FLUID_EXTRACTED,
    /**
     * Used when a crafting task is cancelled in a crafting monitor.
     */
    CRAFTING_TASK_CANCELLED,
    /**
     * Used when all crafting tasks are cancelled in a crafting monitor.
     */
    CRAFTING_TASK_ALL_CANCELLED
}
