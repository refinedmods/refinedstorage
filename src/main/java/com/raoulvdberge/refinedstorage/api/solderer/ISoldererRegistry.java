package com.raoulvdberge.refinedstorage.api.solderer;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * The recipe registry of the solderer.
 */
public interface ISoldererRegistry {
    /**
     * Adds a recipe to the registry.
     *
     * @param recipe the recipe to add
     */
    void addRecipe(@Nonnull ISoldererRecipe recipe);

    /**
     * Returns a solderer recipe from the rows.
     *
     * @param rows an item handler, where slots 0 - 2 are the rows
     * @return the recipe, or null if no recipe was found
     */
    @Nullable
    ISoldererRecipe getRecipe(@Nonnull IItemHandler rows);

    /**
     * @return a list with all the solderer recipes
     */
    List<ISoldererRecipe> getRecipes();

    /**
     * Creates a simple solderer recipe.
     *
     * @param result   the result
     * @param duration the duration in ticks
     * @param rows     the rows of this recipe, has to be 3 rows (null for an empty row)
     * @return a solderer recipe
     */
    @Nonnull
    ISoldererRecipe createSimpleRecipe(@Nonnull ItemStack result, int duration, ItemStack... rows);

    /**
     * Remove existing recipes from the solderer
     *
     * @param result the result
     * @param rows none or the three rows that give the result
     * @return a list of removed {@link ISoldererRecipe}s
     */
    List<ISoldererRecipe> removeRecipe(@Nonnull ItemStack result, ItemStack... rows);
}
