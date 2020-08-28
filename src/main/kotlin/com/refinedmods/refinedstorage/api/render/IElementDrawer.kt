package com.refinedmods.refinedstorage.api.render

import com.refinedmods.refinedstorage.api.autocrafting.craftingmonitor.ICraftingMonitorElement
import com.refinedmods.refinedstorage.api.autocrafting.preview.ICraftingPreviewElement
import net.minecraft.client.util.math.MatrixStack


/**
 * This [FunctionalInterface] is used to define a draw/render function.
 * This function use x and y coords and the element to draw.
 * Usually packaged in a [IElementDrawers].
 * Used in [ICraftingPreviewElement.draw] and [ICraftingMonitorElement.draw].
 *
 * @param <T> the element to draw, usually [String], [net.minecraft.item.ItemStack] or [net.minecraftforge.fluids.FluidInstance]
</T> */
@FunctionalInterface
interface IElementDrawer<T> {
    /**
     * @param matrixStack the matrix stack
     * @param x           the x axis
     * @param y           the y axis
     * @param element     the element type
     */
    fun draw(matrixStack: MatrixStack, x: Int, y: Int, element: T?)
}