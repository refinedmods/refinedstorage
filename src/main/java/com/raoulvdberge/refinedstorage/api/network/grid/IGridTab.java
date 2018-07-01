package com.raoulvdberge.refinedstorage.api.network.grid;

import com.raoulvdberge.refinedstorage.api.util.IFilter;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;

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
     * @param x            the x position
     * @param y            the y position
     * @param screenWidth  the screen width
     * @param screenHeight the screen height
     * @param fontRenderer the font renderer
     */
    void drawTooltip(int x, int y, int screenWidth, int screenHeight, FontRenderer fontRenderer);

    /**
     * @return the icon
     */
    ItemStack getIcon();
}
