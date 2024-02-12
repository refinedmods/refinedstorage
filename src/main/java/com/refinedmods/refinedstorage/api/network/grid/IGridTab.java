package com.refinedmods.refinedstorage.api.network.grid;

import com.refinedmods.refinedstorage.api.render.IElementDrawer;
import com.refinedmods.refinedstorage.api.util.IFilter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import java.util.List;

/**
 * Represents a grid tab.
 */
public interface IGridTab {
    int TAB_WIDTH = 28;
    int TAB_HEIGHT = 31;

    /**
     * @return the filters
     */
    List<IFilter> getFilters();

    /**
     * Draws the tooltip of this tab at the given position.
     *
     * @param font     the font
     * @param graphics the graphics
     * @param x        the x position
     * @param y        the y position
     */
    void drawTooltip(Font font, GuiGraphics graphics, int x, int y);

    /**
     * Draws the icon.
     *
     * @param graphics the graphics
     * @param x        the x position
     * @param y        the y position
     */
    void drawIcon(GuiGraphics graphics, int x, int y, IElementDrawer<ItemStack> itemDrawer, IElementDrawer<FluidStack> fluidDrawer);
}
