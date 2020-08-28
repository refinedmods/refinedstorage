package com.refinedmods.refinedstorage.api.network.grid

import com.refinedmods.refinedstorage.api.util.IStackList
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.recipe.CraftingRecipe


/**
 * Defines default behavior of crafting grids.
 */
interface ICraftingGridBehavior {
    /**
     * Logic for regular crafting.
     *
     * @param grid           the grid
     * @param recipe         the recipe
     * @param player         the player
     * @param availableItems the items available for shift crafting
     * @param usedItems      the items used by shift crafting
     */
    fun onCrafted(grid: INetworkAwareGrid, recipe: CraftingRecipe, player: PlayerEntity, availableItems: IStackList<ItemStack>?, usedItems: IStackList<ItemStack>?)

    /**
     * Logic for crafting with shift click (mass crafting).
     *
     * @param grid   the grid
     * @param player the player
     */
    fun onCraftedShift(grid: INetworkAwareGrid, player: PlayerEntity)

    /**
     * Logic for when a recipe is transferred to the grid.
     *
     * @param grid   the grid
     * @param player the player
     * @param recipe the recipe
     */
    fun onRecipeTransfer(grid: INetworkAwareGrid, player: PlayerEntity, recipe: Array<Array<ItemStack>>)
}