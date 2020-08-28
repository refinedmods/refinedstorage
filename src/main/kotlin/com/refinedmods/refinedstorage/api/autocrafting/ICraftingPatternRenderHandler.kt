package com.refinedmods.refinedstorage.api.autocrafting

import net.minecraft.item.ItemStack


/**
 * Defines the behavior of pattern rendering.
 * One can add this interface through [IRSAPI.addPatternRenderHandler].
 */
interface ICraftingPatternRenderHandler {
    /**
     * Returns true if the pattern can render its output.
     * As soon as one [ICraftingPatternRenderHandler] returns true for this method, it will render the output.
     *
     * @param pattern the pattern
     * @return true if this pattern can render its output, false otherwise
     */
    fun canRenderOutput(pattern: ItemStack?): Boolean
}