package com.refinedmods.refinedstorage.screen

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.container.WirelessTransmitterContainer
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton
import com.refinedmods.refinedstorage.tile.WirelessTransmitterTile
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.text.Text

class WirelessTransmitterScreen(container: WirelessTransmitterContainer, inventory: PlayerInventory?, title: Text?) : BaseScreen<WirelessTransmitterContainer?>(container, 211, 137, inventory, title) {
    override fun onPostInit(x: Int, y: Int) {
        addSideButton(RedstoneModeSideButton(this, WirelessTransmitterTile.REDSTONE_MODE))
    }

    override fun tick(x: Int, y: Int) {}
    override fun renderBackground(matrixStack: MatrixStack?, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        bindTexture(RS.ID, "gui/wireless_transmitter.png")
        blit(matrixStack, x, y, 0, 0, xSize, ySize)
    }

    override fun renderForeground(matrixStack: MatrixStack?, mouseX: Int, mouseY: Int) {
        renderString(matrixStack, 7, 7, title.getString())
        renderString(matrixStack, 28, 25, I18n.format("gui.refinedstorage.wireless_transmitter.distance", WirelessTransmitterTile.RANGE.value))
        renderString(matrixStack, 7, 43, I18n.format("container.inventory"))
    }
}