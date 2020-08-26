package com.refinedmods.refinedstorage.apiimpl.render

import com.refinedmods.refinedstorage.api.render.IElementDrawer
import com.refinedmods.refinedstorage.api.render.IElementDrawers
import com.refinedmods.refinedstorage.render.FluidRenderer
import com.refinedmods.refinedstorage.screen.BaseScreen
import net.minecraft.client.gui.FontRenderer
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidInstance


open class ElementDrawers(protected val screen: BaseScreen<*>, fontRenderer: FontRenderer) : IElementDrawers {
    private override val fontRenderer: FontRenderer
    override val itemDrawer: IElementDrawer<ItemStack>
        get() = screen::renderItem
    override val fluidDrawer: IElementDrawer<Any>
        get() = IElementDrawer<FluidInstance> { matrixStack: MatrixStack?, xPosition: Int, yPosition: Int, fluidStack: FluidInstance? -> FluidRenderer.INSTANCE.render(matrixStack, xPosition, yPosition, fluidStack) }
    override val stringDrawer: IElementDrawer<String>
        get() = screen::renderString

    override fun getFontRenderer(): FontRenderer {
        return fontRenderer
    }

    init {
        this.fontRenderer = fontRenderer
    }
}