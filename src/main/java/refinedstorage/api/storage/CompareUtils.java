package refinedstorage.api.storage;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Comparison utils.
 */
public final class CompareUtils {
    public static final int COMPARE_DAMAGE = 1;
    public static final int COMPARE_NBT = 2;
    public static final int COMPARE_QUANTITY = 4;

    public static boolean compareStack(ItemStack left, ItemStack right) {
        return compareStack(left, right, COMPARE_NBT | COMPARE_DAMAGE | COMPARE_QUANTITY);
    }

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

    public static boolean compareStackNoQuantity(ItemStack left, ItemStack right) {
        return compareStack(left, right, COMPARE_NBT | COMPARE_DAMAGE);
    }

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
