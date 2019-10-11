package com.raoulvdberge.refinedstorage.api.network.grid;

/**
 * A crafting listener for the grid.
 */
public interface ICraftingGridListener {
    /**
     * Called when the crafting matrix was changed.
     * Usually you'd send slot updates for the crafting slots (and output slot) here, so that all clients get an actual view.
     * This listener exists so the crafting result slot is only calculated on the server.
     */
    void onCraftingMatrixChanged();
}
