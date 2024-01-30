package com.refinedmods.refinedstorage.api.util;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import javax.annotation.Nonnull;

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
    boolean isEqual(@Nonnull ItemStack left, @Nonnull ItemStack right, int flags);

    /**
     * Compares two stacks by NBT, damage and quantity.
     *
     * @param left  the left stack
     * @param right the right stack
     * @return true if the left and right stack are the same, false otherwise
     */
    default boolean isEqual(@Nonnull ItemStack left, @Nonnull ItemStack right) {
        return isEqual(left, right, COMPARE_NBT | COMPARE_QUANTITY);
    }

    /**
     * Compares two stacks by NBT and damage.
     *
     * @param left  the left stack
     * @param right the right stack
     * @return true if the left and right stack are the same, false otherwise
     */
    default boolean isEqualNoQuantity(@Nonnull ItemStack left, @Nonnull ItemStack right) {
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
    boolean isEqual(@Nonnull FluidStack left, @Nonnull FluidStack right, int flags);
}
