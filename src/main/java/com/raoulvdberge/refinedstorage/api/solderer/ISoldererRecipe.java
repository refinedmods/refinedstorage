package com.raoulvdberge.refinedstorage.api.solderer;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;

/**
 * Represents a recipe in the solderer.
 */
public interface ISoldererRecipe {
    /**
     * @param row the row in the solderer that we want the stack for (between 0 - 2)
     * @return possible stack(s) for the given row, or empty list for no stack
     */
    @Nonnull
    NonNullList<ItemStack> getRow(int row);

    /**
     * @return the stack that this recipe gives back
     */
    @Nonnull
    ItemStack getResult();

    /**
     * @return the duration in ticks that this recipe takes to give the result back
     */
    int getDuration();
}
