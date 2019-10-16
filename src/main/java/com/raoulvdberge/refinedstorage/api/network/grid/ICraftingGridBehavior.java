package com.raoulvdberge.refinedstorage.api.network.grid;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;

/**
 * Defines default behavior of crafting grids.
 */
public interface ICraftingGridBehavior {
    /**
     * Logic for regular crafting.
     *
     * @param grid   the grid
     * @param recipe the recipe
     * @param player the player
     */
    void onCrafted(IGridNetworkAware grid, ICraftingRecipe recipe, PlayerEntity player);

    /**
     * Logic for crafting with shift click (mass crafting).
     *
     * @param grid   the grid
     * @param player the player
     */
    void onCraftedShift(IGridNetworkAware grid, PlayerEntity player);

    /**
     * Logic for when a recipe is transferred to the grid.
     *
     * @param grid   the grid
     * @param player the player
     * @param recipe the recipe
     */
    void onRecipeTransfer(IGridNetworkAware grid, PlayerEntity player, ItemStack[][] recipe);
}
