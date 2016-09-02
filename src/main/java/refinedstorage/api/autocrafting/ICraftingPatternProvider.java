package refinedstorage.api.autocrafting;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Implement this interface on pattern items.
 * When you implement this interface on your patterns, they will be insertable in crafters.
 */
public interface ICraftingPatternProvider {
    /**
     * Creates a crafting pattern.
     *
     * @param world     The world
     * @param stack     The pattern stack
     * @param container The container where the pattern is in
     * @return The crafting pattern
     */
    @Nonnull
    ICraftingPattern create(World world, ItemStack stack, ICraftingPatternContainer container);
}
