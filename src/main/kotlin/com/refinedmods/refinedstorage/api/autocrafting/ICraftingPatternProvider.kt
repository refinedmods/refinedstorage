package com.refinedmods.refinedstorage.api.autocrafting

import net.minecraft.item.ItemStack
import net.minecraft.world.World


/**
 * Implement this interface on crafting pattern items.
 * When this interface is implemented on the item in question, they will be insertable in crafters.
 */
interface ICraftingPatternProvider {
    /**
     * Creates a crafting pattern.
     *
     * @param world     the world
     * @param stack     the pattern stack, the implementor needs to copy it
     * @param container the [ICraftingPatternContainer] where the pattern is in
     * @return the crafting pattern
     */
    fun create(world: World, stack: ItemStack, container: ICraftingPatternContainer): ICraftingPattern
}