package com.refinedmods.refinedstorage.api.network.grid;

import com.mojang.blaze3d.vertex.PoseStack;
import com.refinedmods.refinedstorage.api.render.IElementDrawer;
import com.refinedmods.refinedstorage.api.util.IFilter;
import net.minecraft.client.gui.Font;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

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
     * @param matrixStack  the matrix stack
     * @param x            the x position
     * @param y            the y position
     * @param screenWidth  the screen width
     * @param screenHeight the screen height
     * @param fontRenderer the font renderer
     */
    void drawTooltip(PoseStack matrixStack, int x, int y, int screenWidth, int screenHeight, Font fontRenderer);

    /**
     * Draws the icon.
     *
     * @param matrixStack the matrix stack
     * @param x           the x position
     * @param y           the y position
     */
    void drawIcon(PoseStack matrixStack, int x, int y, IElementDrawer<ItemStack> itemDrawer, IElementDrawer<FluidStack> fluidDrawer);
}
