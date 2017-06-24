package com.raoulvdberge.refinedstorage.api.solderer;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
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
     * @param ingredients an item handler, where slots 0 - 2 are the ingredient rows
     * @return the recipe, or null if no recipe was found
     */
    @Nullable
    ISoldererRecipe getRecipe(@Nonnull IItemHandler ingredients);

    /**
     * @return a list with all the solderer recipes
     */
    List<ISoldererRecipe> getRecipes();

    /**
     * Creates a simple solderer recipe.
     *
     * @param result   the result
     * @param duration the duration in ticks
     * @param rows     the rows of this recipe, has to be 3 rows (empty list for empty row)
     * @return a solderer recipe
     */
    @Nonnull
    ISoldererRecipe createSimpleRecipe(@Nonnull ItemStack result, int duration, NonNullList<ItemStack>... rows);
}
