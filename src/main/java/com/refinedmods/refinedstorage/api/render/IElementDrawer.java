package com.refinedmods.refinedstorage.api.render;

import net.minecraft.client.gui.GuiGraphics;

/**
 * This {@link FunctionalInterface} is used to define a draw/render function.
 * This function use x and y coords and the element to draw.
 *
 * @param <T> the element to draw, usually {@link String}, {@link net.minecraft.world.item.ItemStack} or {@link net.minecraftforge.fluids.FluidStack}
 */
@FunctionalInterface
public interface IElementDrawer<T> {
    /**
     * @param graphics the graphics
     * @param x        the x axis
     * @param y        the y axis
     * @param element  the element type
     */
    void draw(GuiGraphics graphics, int x, int y, T element);
}
