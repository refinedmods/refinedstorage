package com.refinedmods.refinedstorage.api.network.grid

import com.refinedmods.refinedstorage.api.render.IElementDrawer
import com.refinedmods.refinedstorage.api.util.IFilter
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import reborncore.common.fluid.container.FluidInstance
import java.awt.font.FontRenderContext


/**
 * Represents a grid tab.
 */
interface IGridTab {
    /**
     * @return the filters
     */
    val filters: List<IFilter<*>>

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
    fun drawTooltip(matrixStack: MatrixStack, x: Int, y: Int, screenWidth: Int, screenHeight: Int, fontRenderer:  FontRenderContext)

    /**
     * Draws the icon.
     *
     * @param matrixStack the matrix stack
     * @param x           the x position
     * @param y           the y position
     */
    fun drawIcon(matrixStack: MatrixStack, x: Int, y: Int, itemDrawer: IElementDrawer<ItemStack>, fluidDrawer: IElementDrawer<FluidInstance>)

    companion object {
        const val TAB_WIDTH = 28
        const val TAB_HEIGHT = 31
    }
}