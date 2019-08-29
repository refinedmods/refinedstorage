package com.raoulvdberge.refinedstorage.apiimpl.util;

import com.raoulvdberge.refinedstorage.api.util.IComparer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class Comparer implements IComparer {
    @Override
    public boolean isEqual(@Nullable ItemStack left, @Nullable ItemStack right, int flags) {
        ActionResultType validity = getResult(left, right);

        if (validity == ActionResultType.FAIL || validity == ActionResultType.SUCCESS) {
            return validity == ActionResultType.SUCCESS;
        }

        if (left.getItem() != right.getItem()) {
            return false;
        }

        if ((flags & COMPARE_NBT) == COMPARE_NBT) {
            if (!isEqualNbt(left, right)) {
                return false;
            }
        }

        if ((flags & COMPARE_QUANTITY) == COMPARE_QUANTITY) {
            if (left.getCount() != right.getCount()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isEqual(@Nullable FluidStack left, @Nullable FluidStack right, int flags) {
        if (left == null && right == null) {
            return true;
        }

        if ((left == null && right != null) || (left != null && right == null)) {
            return false;
        }

        if (left.getFluid() != right.getFluid()) {
            return false;
        }

        if ((flags & COMPARE_QUANTITY) == COMPARE_QUANTITY) {
            if (left.getAmount() != right.getAmount()) {
                return false;
            }
        }

        if ((flags & COMPARE_NBT) == COMPARE_NBT) {
            if (left.getTag() != null && !left.getTag().equals(right.getTag())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isEqualNbt(@Nullable ItemStack left, @Nullable ItemStack right) {
        ActionResultType validity = getResult(left, right);

        if (validity == ActionResultType.FAIL || validity == ActionResultType.SUCCESS) {
            return validity == ActionResultType.SUCCESS;
        }

        if (!ItemStack.areItemStackTagsEqual(left, right)) {
            if (left.hasTag() && !right.hasTag() && left.getTag().isEmpty()) {
                return true;
            } else if (!left.hasTag() && right.hasTag() && right.getTag().isEmpty()) {
                return true;
            } else if (!left.hasTag() && !right.hasTag()) {
                return true;
            }

            return false;
        }

        return true;
    }

    private ActionResultType getResult(@Nullable ItemStack left, @Nullable ItemStack right) {
        if (left == null && right == null) {
            return ActionResultType.SUCCESS;
        }

        if ((left == null && right != null) || (left != null && right == null)) {
            return ActionResultType.FAIL;
        }

        boolean leftEmpty = left.isEmpty();
        boolean rightEmpty = right.isEmpty();

        if (leftEmpty && rightEmpty) {
            return ActionResultType.SUCCESS;
        }

        if ((leftEmpty && !rightEmpty) || (!leftEmpty && rightEmpty)) {
            return ActionResultType.FAIL;
        }

        return ActionResultType.PASS;
    }
}
