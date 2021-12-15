package com.refinedmods.refinedstorage.api.render;

import net.minecraft.world.item.ItemStack;
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
     * @return an overlay drawer
     */
    default IElementDrawer<Integer> getOverlayDrawer() {
        return getNullDrawer();
    }

    default IElementDrawer<Void> getErrorDrawer() {
        return getNullDrawer();
    }

    /**
     * DO NOT OVERRIDE!
     *
     * @param <T> any type of drawer
     * @return a drawer that does nothing
     */
    default <T> IElementDrawer<T> getNullDrawer() {
        return (poseStack, x, y, element) -> {
            // NO OP
        };
    }
}
