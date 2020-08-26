package com.refinedmods.refinedstorage.screen

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.container.NetworkTransmitterContainer
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton
import com.refinedmods.refinedstorage.tile.NetworkTransmitterTile
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.Identifier
import net.minecraft.util.text.Text
import java.util.*

class NetworkTransmitterScreen(container: NetworkTransmitterContainer, inventory: PlayerInventory?, title: Text?) : BaseScreen<NetworkTransmitterContainer?>(container, 176, 137, inventory, title) {
    override fun onPostInit(x: Int, y: Int) {
        addSideButton(RedstoneModeSideButton(this, NetworkTransmitterTile.REDSTONE_MODE))
    }

    override fun tick(x: Int, y: Int) {}
    override fun renderBackground(matrixStack: MatrixStack?, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        bindTexture(RS.ID, "gui/network_transmitter.png")
        blit(matrixStack, x, y, 0, 0, xSize, ySize)
    }

    override fun renderForeground(matrixStack: MatrixStack?, mouseX: Int, mouseY: Int) {
        renderString(matrixStack, 7, 7, title.getString())
        val text: String
        val receiverDim: Optional<Identifier> = NetworkTransmitterTile.RECEIVER_DIMENSION.value
        val distance = NetworkTransmitterTile.DISTANCE.value
        text = if (!receiverDim.isPresent()) {
            I18n.format("gui.refinedstorage.network_transmitter.missing_card")
        } else if (distance != -1) {
            I18n.format("gui.refinedstorage.network_transmitter.distance", distance)
        } else {
            receiverDim.get().toString()
        }
        renderString(matrixStack, 51, 24, text)
        renderString(matrixStack, 7, 42, I18n.format("container.inventory"))
    }
}