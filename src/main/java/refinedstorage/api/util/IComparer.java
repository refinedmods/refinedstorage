package refinedstorage.api.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/**
 * Utilities for comparing item and fluid stacks.
 */
public interface IComparer {
    int COMPARE_DAMAGE = 1;
    int COMPARE_NBT = 2;
    int COMPARE_QUANTITY = 4;
    int COMPARE_OREDICT = 8;

    /**
     * Compares two stacks by the given flags.
     *
     * @param left  the left stack
     * @param right the right stack
     * @param flags the flags to compare with
     * @return true if the left and right stack are the same, false otherwise
     */
    boolean isEqual(ItemStack left, ItemStack right, int flags);

    /**
     * Compares two stacks by NBT, damage and quantity.
     *
     * @param left  the left stack
     * @param right the right stack
     * @return true if the left and right stack are the same, false otherwise
     */
    default boolean isEqual(ItemStack left, ItemStack right) {
        return isEqual(left, right, COMPARE_NBT | COMPARE_DAMAGE | COMPARE_QUANTITY);
    }

    /**
     * Compares two stacks by NBT and damage.
     *
     * @param left  the left stack
     * @param right the right stack
     * @return true if the left and right stack are the same, false otherwise
     */
    default boolean isEqualNoQuantity(ItemStack left, ItemStack right) {
        return isEqual(left, right, COMPARE_NBT | COMPARE_DAMAGE);
    }

    /**
     * Compares two stacks by the given flags.
     *
     * @param left  the left stack
     * @param right the right stack
     * @param flags the flags to compare with
     * @return true if the left and right stack are the same, false otherwise
     */
    boolean isEqual(FluidStack left, FluidStack right, int flags);

    /**
     * Compares the NBT tags of two stacks.
     *
     * @param left  the left stack
     * @param right the right stack
     * @return true if the NBT tags of the two stacks are the same, false otherwise
     */
    boolean isEqualNBT(ItemStack left, ItemStack right);

    /**
     * Compares two stacks and checks if they share the same ore dictionary entry.
     *
     * @param left  the left stack
     * @param right the right stack
     * @return true if the two stacks share the same ore dictionary entry
     */
    boolean isEqualOredict(ItemStack left, ItemStack right);
}
