package refinedstorage.api.autocrafting;

import net.minecraft.item.ItemStack;

/**
 * Implement this interface on pattern items.
 * When you implement this interface on your patterns, they will be insertable in crafters.
 *
 * @todo: Serialization from controller!
 */
public interface ICraftingPatternProvider {
    /**
     * Creates a crafting pattern.
     *
     * @param stack     The pattern stack
     * @param container The container where the pattern is in
     * @return The crafting pattern
     */
    ICraftingPattern create(ItemStack stack, ICraftingPatternContainer container);
}
