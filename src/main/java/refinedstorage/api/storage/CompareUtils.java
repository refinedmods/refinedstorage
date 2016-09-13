package refinedstorage.api.storage;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Utilities for comparing item and fluid stacks.
 */
public final class CompareUtils {
    public static final int COMPARE_DAMAGE = 1;
    public static final int COMPARE_NBT = 2;
    public static final int COMPARE_QUANTITY = 4;

    /**
     * Compares two stacks by NBT, damage and quantity.
     *
     * @param left  the left stack
     * @param right the right stack
     * @return true if the left and right stack are the same, false otherwise
     */
    public static boolean compareStack(ItemStack left, ItemStack right) {
        return compareStack(left, right, COMPARE_NBT | COMPARE_DAMAGE | COMPARE_QUANTITY);
    }

    /**
     * Compares two stacks by NBT and damage.
     *
     * @param left  the left stack
     * @param right the right stack
     * @return true if the left and right stack are the same, false otherwise
     */
    public static boolean compareStackNoQuantity(ItemStack left, ItemStack right) {
        return compareStack(left, right, COMPARE_NBT | COMPARE_DAMAGE);
    }

    /**
     * Compares two stacks by the given flags.
     *
     * @param left  the left stack
     * @param right the right stack
     * @param flags the flags to compare with
     * @return true if the left and right stack are the same, false otherwise
     */
    public static boolean compareStack(ItemStack left, ItemStack right, int flags) {
        if (left == null && right == null) {
            return true;
        }

        if ((left == null && right != null) || (left != null && right == null)) {
            return false;
        }

        if (left.getItem() != right.getItem()) {
            return false;
        }

        if ((flags & COMPARE_DAMAGE) == COMPARE_DAMAGE) {
            if (left.getItemDamage() != right.getItemDamage()) {
                return false;
            }
        }

        if ((flags & COMPARE_NBT) == COMPARE_NBT) {
            if (!compareNbt(left, right)) {
                return false;
            }
        }

        if ((flags & COMPARE_QUANTITY) == COMPARE_QUANTITY) {
            if (left.stackSize != right.stackSize) {
                return false;
            }
        }

        return true;
    }

    /**
     * Compares two stacks by the given flags.
     *
     * @param left  the left stack
     * @param right the right stack
     * @param flags the flags to compare with
     * @return true if the left and right stack are the same, false otherwise
     */
    public static boolean compareStack(FluidStack left, FluidStack right, int flags) {
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

    /**
     * Compares the NBT tags of two stacks.
     *
     * @param left  the left stack
     * @param right the right stack
     * @return true if the NBT tags of the two stacks are the same, false otherwise
     */
    public static boolean compareNbt(ItemStack left, ItemStack right) {
        if (!ItemStack.areItemStackTagsEqual(left, right)) {
            if (left.hasTagCompound() && !right.hasTagCompound() && left.getTagCompound().hasNoTags()) {
                return true;
            } else if (!left.hasTagCompound() && right.hasTagCompound() && right.getTagCompound().hasNoTags()) {
                return true;
            }

            return false;
        }

        return true;
    }

    /**
     * Compares two stacks and checks if they share the same ore dictionary entry.
     *
     * @param left  the left stack
     * @param right the right stack
     * @return true if the two stacks share the same ore dictionary entry
     */
    public static boolean compareStackOreDict(ItemStack left, ItemStack right) {
        if (left == null && right == null) {
            return true;
        }

        if ((left == null && right != null) || (left != null && right == null)) {
            return false;
        }

        int[] leftIds = OreDictionary.getOreIDs(left);
        int[] rightIds = OreDictionary.getOreIDs(right);

        for (int i : rightIds) {
            if (ArrayUtils.contains(leftIds, i)) {
                return true;
            }
        }

        return false;
    }
}
