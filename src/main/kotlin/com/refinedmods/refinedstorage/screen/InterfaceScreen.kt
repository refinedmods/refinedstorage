package com.refinedmods.refinedstorage.screen

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.container.InterfaceContainer
import com.refinedmods.refinedstorage.screen.widget.sidebutton.ExactModeSideButton
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton
import com.refinedmods.refinedstorage.tile.InterfaceTile
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.text.Text

class InterfaceScreen(container: InterfaceContainer, inventory: PlayerInventory?, title: Text?) : BaseScreen<InterfaceContainer?>(container, 211, 217, inventory, title) {
    override fun onPostInit(x: Int, y: Int) {
        addSideButton(RedstoneModeSideButton(this, InterfaceTile.REDSTONE_MODE))
        addSideButton(ExactModeSideButton(this, InterfaceTile.COMPARE))
    }

    override fun tick(x: Int, y: Int) {}
    override fun renderBackground(matrixStack: MatrixStack?, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        bindTexture(RS.ID, "gui/interface.png")
        blit(matrixStack, x, y, 0, 0, xSize, ySize)
    }

    override fun renderForeground(matrixStack: MatrixStack?, mouseX: Int, mouseY: Int) {
        renderString(matrixStack, 7, 7, I18n.format("gui.refinedstorage.interface.import"))
        renderString(matrixStack, 7, 42, I18n.format("gui.refinedstorage.interface.export"))
        renderString(matrixStack, 7, 122, I18n.format("container.inventory"))
    }
}