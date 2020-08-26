package com.refinedmods.refinedstorage.api.render

import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.ItemStack
import reborncore.common.fluid.container.FluidInstance
import java.awt.font.FontRenderContext


/**
 * Interface specifying default element drawers.
 */
interface IElementDrawers {
    /**
     * @return an item drawer
     */
    val itemDrawer: IElementDrawer<ItemStack>
        get() = getNullDrawer()

    /**
     * @return a fluid drawer
     */
    val fluidDrawer: IElementDrawer<FluidInstance>
        get() = getNullDrawer()

    /**
     * @return a string drawer
     */
    val stringDrawer: IElementDrawer<String>
        get() = getNullDrawer()

    /**
     * @return an overlay drawer, color will be the element
     */
    val overlayDrawer: IElementDrawer<Int>
        get() = getNullDrawer()

    val errorDrawer: IElementDrawer<Any>
        get() = getNullDrawer()

    /**
     * @return the font renderer
     */
    val fontRenderer: FontRenderContext?

    /**
     * DO NOT OVERRIDE!
     *
     * @param <T> any type of drawer
     * @return a drawer that does nothing
    </T> */
    fun <T> getNullDrawer(): IElementDrawer<T> {
        return object : IElementDrawer<T> {
            override fun draw(matrixStack: MatrixStack, x: Int, y: Int, element: T?) {}
        }
    }
}