package com.refinedmods.refinedstorage.api.network.grid;

import com.refinedmods.refinedstorage.api.util.IStackList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;

import javax.annotation.Nullable;

/**
 * Defines default behavior of crafting grids.
 */
public interface ICraftingGridBehavior {
    /**
     * Logic for regular crafting.
     *
     * @param grid           the grid
     * @param recipe         the recipe
     * @param player         the player
     * @param availableItems the items available for shift crafting
     * @param usedItems      the items used by shift crafting
     */
    void onCrafted(INetworkAwareGrid grid, CraftingRecipe recipe, Player player, @Nullable IStackList<ItemStack> availableItems, @Nullable IStackList<ItemStack> usedItems);

    /**
     * Logic for crafting with shift click (mass crafting).
     *
     * @param grid   the grid
     * @param player the player
     */
    void onCraftedShift(INetworkAwareGrid grid, Player player);

    /**
     * Logic for when a recipe is transferred to the grid.
     *
     * @param grid   the grid
     * @param player the player
     * @param recipe the recipe
     */
    void onRecipeTransfer(INetworkAwareGrid grid, Player player, ItemStack[][] recipe);
}
