package com.refinedmods.refinedstorage.apiimpl.render

import com.mojang.blaze3d.systems.RenderSystem
import com.refinedmods.refinedstorage.api.render.IElementDrawer
import com.refinedmods.refinedstorage.screen.grid.CraftingPreviewScreen
import net.minecraft.client.gui.AbstractGui
import net.minecraft.client.gui.FontRenderer


class CraftingPreviewElementDrawers(screen: CraftingPreviewScreen, fontRenderer: FontRenderer) : ElementDrawers(screen, fontRenderer) {
    override val overlayDrawer: IElementDrawer<Int> = IElementDrawer<Int> { matrixStack: error.NonExistentClass?, x: Int, y: Int, color: Int? ->
        RenderSystem.color4f(1f, 1f, 1f, 1f)
        RenderSystem.disableLighting()
        AbstractGui.fill(matrixStack, x, y, x + 73, y + 29, color)
    }
}