package com.refinedmods.refinedstorage.api.network.grid;

import com.refinedmods.refinedstorage.api.util.IStackList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;

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
    void onCrafted(INetworkAwareGrid grid, ICraftingRecipe recipe, PlayerEntity player, @Nullable IStackList<ItemStack> availableItems, @Nullable IStackList<ItemStack> usedItems);

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
