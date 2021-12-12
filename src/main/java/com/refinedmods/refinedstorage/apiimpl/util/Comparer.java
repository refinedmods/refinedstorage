package com.refinedmods.refinedstorage.apiimpl.util;

import com.refinedmods.refinedstorage.api.util.IComparer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class Comparer implements IComparer {
    @Override
    public boolean isEqual(@Nonnull ItemStack left, @Nonnull ItemStack right, int flags) {
        if (left.isEmpty() && right.isEmpty()) {
            return true;
        }

        if (!ItemStack.isSame(left, right)) {
            return false;
        }

        if ((flags & COMPARE_NBT) == COMPARE_NBT && !ItemStack.tagMatches(left, right)) {
            return false;
        }

        if ((flags & COMPARE_QUANTITY) == COMPARE_QUANTITY && left.getCount() != right.getCount()) {
            return false;
        }

        return true;
    }

    @Override
    public boolean isEqual(@Nonnull FluidStack left, @Nonnull FluidStack right, int flags) {
        if (left.isEmpty() && right.isEmpty()) {
            return true;
        }

        if (left.getFluid() != right.getFluid()) {
            return false;
        }

        if ((flags & COMPARE_NBT) == COMPARE_NBT && !FluidStack.areFluidStackTagsEqual(left, right)) {
            return false;
        }

        if ((flags & COMPARE_QUANTITY) == COMPARE_QUANTITY && left.getAmount() != right.getAmount()) {
            return false;
        }

        return true;
    }
}
