package com.raoulvdberge.refinedstorage.apiimpl.util;

import com.raoulvdberge.refinedstorage.api.util.IComparer;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;

public class Comparer implements IComparer {
    @Override
    public boolean isEqual(@Nullable ItemStack left, @Nullable ItemStack right, int flags) {
        EnumActionResult validity = validityCheck(left, right);

        if (validity == EnumActionResult.FAIL || validity == EnumActionResult.SUCCESS) {
            return validity == EnumActionResult.SUCCESS;
        }

        if ((flags & COMPARE_OREDICT) == COMPARE_OREDICT) {
            if (isEqualOredict(left, right)) {
                return true;
            }
        }

        if (left.getItem() != right.getItem()) {
            return false;
        }

        if ((flags & COMPARE_DAMAGE) == COMPARE_DAMAGE && left.getItemDamage() != OreDictionary.WILDCARD_VALUE && right.getItemDamage() != OreDictionary.WILDCARD_VALUE) {
            if (left.getItemDamage() != right.getItemDamage()) {
                return false;
            }
        }

        if ((flags & COMPARE_NBT) == COMPARE_NBT) {
            if (!isEqualNBT(left, right)) {
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
            if (left.amount != right.amount) {
                return false;
            }
        }

        if ((flags & COMPARE_NBT) == COMPARE_NBT) {
            if (left.tag != null && !left.tag.equals(right.tag)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isEqualNBT(@Nullable ItemStack left, @Nullable ItemStack right) {
        EnumActionResult validity = validityCheck(left, right);

        if (validity == EnumActionResult.FAIL || validity == EnumActionResult.SUCCESS) {
            return validity == EnumActionResult.SUCCESS;
        }

        if (!ItemStack.areItemStackTagsEqual(left, right)) {
            if (left.hasTagCompound() && !right.hasTagCompound() && left.getTagCompound().hasNoTags()) {
                return true;
            } else if (!left.hasTagCompound() && right.hasTagCompound() && right.getTagCompound().hasNoTags()) {
                return true;
            } else if (!left.hasTagCompound() && !right.hasTagCompound()) {
                return true;
            }

            return false;
        }

        return true;
    }

    @Override
    public boolean isEqualOredict(@Nullable ItemStack left, @Nullable ItemStack right) {
        EnumActionResult validity = validityCheck(left, right);

        if (validity == EnumActionResult.FAIL || validity == EnumActionResult.SUCCESS) {
            return validity == EnumActionResult.SUCCESS;
        }

        return StackUtils.areStacksEquivalent(left, right);
    }

    private EnumActionResult validityCheck(@Nullable ItemStack left, @Nullable ItemStack right) {
        if (left == null && right == null) {
            return EnumActionResult.SUCCESS;
        }

        if ((left == null && right != null) || (left != null && right == null)) {
            return EnumActionResult.FAIL;
        }

        boolean leftEmpty = left.isEmpty();
        boolean rightEmpty = right.isEmpty();

        if (leftEmpty && rightEmpty) {
            return EnumActionResult.SUCCESS;
        }

        if ((leftEmpty && !rightEmpty) || (!leftEmpty && rightEmpty)) {
            return EnumActionResult.FAIL;
        }

        return EnumActionResult.PASS;
    }
}
