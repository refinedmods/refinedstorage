package com.raoulvdberge.refinedstorage.api.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

/**
 * Utilities for comparing item and fluid stacks.
 */
public interface IComparer {
    int COMPARE_NBT = 1;
    int COMPARE_QUANTITY = 2;

    /**
     * Compares two stacks by the given flags.
     *
     * @param left  the left stack
     * @param right the right stack
     * @param flags the flags to compare with
     * @return true if the left and right stack are the same, false otherwise
     */
    boolean isEqual(@Nullable ItemStack left, @Nullable ItemStack right, int flags);

    /**
     * Compares two stacks by NBT, damage and quantity.
     *
     * @param left  the left stack
     * @param right the right stack
     * @return true if the left and right stack are the same, false otherwise
     */
    default boolean isEqual(@Nullable ItemStack left, @Nullable ItemStack right) {
        return isEqual(left, right, COMPARE_NBT | COMPARE_QUANTITY);
    }

    /**
     * Compares two stacks by NBT and damage.
     *
     * @param left  the left stack
     * @param right the right stack
     * @return true if the left and right stack are the same, false otherwise
     */
    default boolean isEqualNoQuantity(@Nullable ItemStack left, @Nullable ItemStack right) {
        return isEqual(left, right, COMPARE_NBT);
    }

    /**
     * Compares two stacks by the given flags.
     *
     * @param left  the left stack
     * @param right the right stack
     * @param flags the flags to compare with
     * @return true if the left and right stack are the same, false otherwise
     */
    boolean isEqual(@Nullable FluidStack left, @Nullable FluidStack right, int flags);

    /**
     * Compares the NBT tags of two stacks.
     *
     * @param left  the left stack
     * @param right the right stack
     * @return true if the NBT tags of the two stacks are the same, false otherwise
     */
    boolean isEqualNbt(@Nullable ItemStack left, @Nullable ItemStack right);
}
