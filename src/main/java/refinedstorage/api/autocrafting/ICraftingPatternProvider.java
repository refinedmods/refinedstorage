package refinedstorage.api.autocrafting;

import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * Implement this interface on pattern items.
 * When you implement this interface on your patterns, they will be insertable in crafters.
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

    /**
     * Creates a crafting pattern from the pattern item stack.
     *
     * @param stack The item stack
     * @return The crafting pattern, or null if the read failed
     */
    @Nullable
    ICraftingPattern create(ItemStack stack);
}
