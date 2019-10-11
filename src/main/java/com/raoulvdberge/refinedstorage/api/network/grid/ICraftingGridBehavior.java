package com.raoulvdberge.refinedstorage.api.network.grid;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.crafting.ICraftingRecipe;

/**
 * Defines default behavior of crafting grids.
 */
public interface ICraftingGridBehavior {
    /**
     * Default logic for regular crafting.
     *
     * @param grid   the grid
     * @param recipe the recipe
     * @param player the player
     */
    void onCrafted(IGridNetworkAware grid, ICraftingRecipe recipe, PlayerEntity player);

    /**
     * Default logic for crafting with shift click (mass crafting).
     *
     * @param grid   the grid
     * @param player the layer
     */
    void onCraftedShift(IGridNetworkAware grid, PlayerEntity player);
}
