package com.refinedmods.refinedstorage.apiimpl.util

import com.refinedmods.refinedstorage.api.util.IComparer
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidInstance


class Comparer : IComparer {
    override fun isEqual(@Nonnull left: ItemStack, @Nonnull right: ItemStack, flags: Int): Boolean {
        if (left.isEmpty && right.isEmpty) {
            return true
        }
        if (!ItemStack.areItemsEqual(left, right)) {
            return false
        }
        if (flags and IComparer.COMPARE_NBT == IComparer.COMPARE_NBT) {
            if (!ItemStack.areItemStackTagsEqual(left, right)) {
                return false
            }
        }
        if (flags and IComparer.COMPARE_QUANTITY == IComparer.COMPARE_QUANTITY) {
            if (left.count != right.count) {
                return false
            }
        }
        return true
    }

    fun isEqual(@Nonnull left: FluidInstance, @Nonnull right: FluidInstance, flags: Int): Boolean {
        if (left.isEmpty() && right.isEmpty()) {
            return true
        }
        if (left.getFluid() !== right.getFluid()) {
            return false
        }
        if (flags and IComparer.COMPARE_NBT == IComparer.COMPARE_NBT) {
            if (!FluidInstance.areFluidInstanceTagsEqual(left, right)) {
                return false
            }
        }
        if (flags and IComparer.COMPARE_QUANTITY == IComparer.COMPARE_QUANTITY) {
            if (left.getAmount() !== right.getAmount()) {
                return false
            }
        }
        return true
    }
}