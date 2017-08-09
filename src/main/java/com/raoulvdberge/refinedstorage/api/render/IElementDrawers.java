package com.raoulvdberge.refinedstorage.api.render;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/**
 * Interface specifying default element drawers.
 */
public interface IElementDrawers {
    /**
     * @return an item drawer
     */
    default IElementDrawer<ItemStack> getItemDrawer() {
        return getNullDrawer();
    }

    /**
     * @return a fluid drawer
     */
    default IElementDrawer<FluidStack> getFluidDrawer() {
        return getNullDrawer();
    }

    /**
     * @return a string drawer
     */
    default IElementDrawer<String> getStringDrawer() {
        return getNullDrawer();
    }

    /**
     * @return an overlay drawer, colour will be the element
     */
    default IElementDrawer<Integer> getOverlayDrawer() {
        return getNullDrawer();
    }

    /**
     * @return the font renderer
     */
    FontRenderer getFontRenderer();

    /**
     * DO NOT OVERRIDE!
     *
     * @param <T> any type of drawer
     * @return a drawer that does nothing
     */
    default <T> IElementDrawer<T> getNullDrawer() {
        return (x, y, element) -> {
            // NO OP
        };
    }
}
