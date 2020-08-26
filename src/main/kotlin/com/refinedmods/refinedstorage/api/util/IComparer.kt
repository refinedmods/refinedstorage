package com.refinedmods.refinedstorage.api.util

import net.minecraft.item.ItemStack
import reborncore.common.fluid.container.FluidInstance

/**
 * Utilities for comparing item and fluid stacks.
 */
interface IComparer {
    /**
     * Compares two stacks by the given flags.
     *
     * @param left  the left stack
     * @param right the right stack
     * @param flags the flags to compare with
     * @return true if the left and right stack are the same, false otherwise
     */
    fun isEqual(left: ItemStack, right: ItemStack, flags: Int): Boolean

    /**
     * Compares two stacks by NBT, damage and quantity.
     *
     * @param left  the left stack
     * @param right the right stack
     * @return true if the left and right stack are the same, false otherwise
     */
    fun isEqual(left: ItemStack, right: ItemStack): Boolean {
        return isEqual(left, right, COMPARE_NBT or COMPARE_QUANTITY)
    }

    /**
     * Compares two stacks by NBT and damage.
     *
     * @param left  the left stack
     * @param right the right stack
     * @return true if the left and right stack are the same, false otherwise
     */
    fun isEqualNoQuantity(left: ItemStack, right: ItemStack): Boolean {
        return isEqual(left, right, COMPARE_NBT)
    }

    /**
     * Compares two stacks by the given flags.
     *
     * @param left  the left stack
     * @param right the right stack
     * @param flags the flags to compare with
     * @return true if the left and right stack are the same, false otherwise
     */
    fun isEqual(left: FluidInstance, right: FluidInstance, flags: Int): Boolean

    companion object {
        const val COMPARE_NBT = 1
        const val COMPARE_QUANTITY = 2
    }
}