package com.refinedmods.refinedstorage.api.network.grid;

import com.refinedmods.refinedstorage.api.util.IStackList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;

/**
 * Defines default behavior of crafting grids.
 */
public interface ICraftingGridBehavior {
    /**
     * Logic for regular crafting.
     * @param grid   the grid
     * @param recipe the recipe
     * @param player the player
     * @param networkItems
     * @param usedItems
     */
    void onCrafted(INetworkAwareGrid grid, ICraftingRecipe recipe, PlayerEntity player, IStackList<ItemStack> networkItems, IStackList<ItemStack> usedItems);

    /**
     * Logic for crafting with shift click (mass crafting).
     *
     * @param grid   the grid
     * @param player the player
     */
    void onCraftedShift(INetworkAwareGrid grid, PlayerEntity player);

    /**
     * Logic for when a recipe is transferred to the grid.
     *
     * @param grid   the grid
     * @param player the player
     * @param recipe the recipe
     */
    void onRecipeTransfer(INetworkAwareGrid grid, PlayerEntity player, ItemStack[][] recipe);
}
