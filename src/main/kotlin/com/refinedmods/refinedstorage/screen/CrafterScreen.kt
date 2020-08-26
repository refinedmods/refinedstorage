package com.refinedmods.refinedstorage.screen

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.container.CrafterContainer
import com.refinedmods.refinedstorage.util.RenderUtils
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.text.Text

class CrafterScreen(container: CrafterContainer, inventory: PlayerInventory?, title: Text?) : BaseScreen<CrafterContainer?>(container, 211, 137, inventory, title) {
    override fun onPostInit(x: Int, y: Int) {}
    override fun tick(x: Int, y: Int) {}
    override fun renderBackground(matrixStack: MatrixStack?, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        bindTexture(RS.ID, "gui/crafter.png")
        blit(matrixStack, x, y, 0, 0, xSize, ySize)
    }

    override fun renderForeground(matrixStack: MatrixStack?, mouseX: Int, mouseY: Int) {
        renderString(matrixStack, 7, 7, RenderUtils.shorten(title.getString(), 26))
        renderString(matrixStack, 7, 43, I18n.format("container.inventory"))
    }
}