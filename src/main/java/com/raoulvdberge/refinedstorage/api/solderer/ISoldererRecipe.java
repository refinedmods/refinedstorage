package com.raoulvdberge.refinedstorage.api.solderer;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * Represents a recipe in the solderer.
 */
public interface ISoldererRecipe {
    /**
     * @return the name of this solderer recipe
     */
    ResourceLocation getName();

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

    /**
     * @return whether this recipe can be used to calculate the EMC value of the resulting item in the Project E mod
     */
    default boolean isProjectERecipe() {
        return true;
    }
}
