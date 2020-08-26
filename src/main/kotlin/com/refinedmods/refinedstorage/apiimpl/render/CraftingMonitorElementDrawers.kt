package com.refinedmods.refinedstorage.apiimpl.render

import com.mojang.blaze3d.systems.RenderSystem
import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.api.render.IElementDrawer
import com.refinedmods.refinedstorage.container.CraftingMonitorContainer
import com.refinedmods.refinedstorage.screen.BaseScreen
import net.minecraft.client.gui.AbstractGui
import net.minecraft.client.gui.FontRenderer


class CraftingMonitorElementDrawers(gui: BaseScreen<CraftingMonitorContainer?>, fontRenderer: FontRenderer, private val itemWidth: Int, private val itemHeight: Int) : ElementDrawers(gui, fontRenderer) {
    override val overlayDrawer: IElementDrawer<Int> = IElementDrawer<Int> { matrixStack: error.NonExistentClass?, x: Int, y: Int, color: Int? ->
        RenderSystem.color4f(1f, 1f, 1f, 1f)
        RenderSystem.disableLighting()
        AbstractGui.fill(matrixStack, x, y, x + itemWidth, y + itemHeight, color)
    }
    override val errorDrawer: IElementDrawer<*> = IElementDrawer<Any> { matrixStack: error.NonExistentClass?, x: Int, y: Int, nothing: Any? ->
        RenderSystem.color4f(1f, 1f, 1f, 1f)
        RenderSystem.disableLighting()
        screen.bindTexture(RS.ID, "gui/crafting_preview.png")
        screen.blit(matrixStack, x + itemWidth - 12 - 2, y + itemHeight - 12 - 2, 0, 244, 12, 12)
    }

}