package com.refinedmods.refinedstorage.api.autocrafting;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;

/**
 * Implement this interface on crafting pattern items.
 * When this interface is implemented on the item in question, they will be insertable in crafters.
 */
public interface ICraftingPatternProvider {
    /**
     * Creates a crafting pattern.
     *
     * @param world     the world
     * @param stack     the pattern stack, the implementor needs to copy it
     * @param container the {@link ICraftingPatternContainer} where the pattern is in
     * @return the crafting pattern
     */
    @Nonnull
    ICraftingPattern create(Level world, ItemStack stack, ICraftingPatternContainer container);
}
