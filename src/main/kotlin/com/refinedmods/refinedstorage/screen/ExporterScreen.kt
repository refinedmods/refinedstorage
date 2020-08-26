package com.refinedmods.refinedstorage.screen

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.container.ExporterContainer
import com.refinedmods.refinedstorage.item.UpgradeItem
import com.refinedmods.refinedstorage.screen.widget.sidebutton.ExactModeSideButton
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton
import com.refinedmods.refinedstorage.screen.widget.sidebutton.TypeSideButton
import com.refinedmods.refinedstorage.tile.ExporterTile
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.text.Text

class ExporterScreen(container: ExporterContainer, playerInventory: PlayerInventory?, title: Text?) : BaseScreen<ExporterContainer?>(container, 211, 137, playerInventory, title) {
    private var hasRegulatorMode: Boolean
    private fun hasRegulatorMode(): Boolean {
        return (container.getTile() as ExporterTile).getNode().getUpgrades().hasUpgrade(UpgradeItem.Type.REGULATOR)
    }

    override fun onPostInit(x: Int, y: Int) {
        addSideButton(RedstoneModeSideButton(this, ExporterTile.REDSTONE_MODE))
        addSideButton(TypeSideButton(this, ExporterTile.TYPE))
        addSideButton(ExactModeSideButton(this, ExporterTile.COMPARE))
    }

    override fun tick(x: Int, y: Int) {
        val updatedHasRegulatorMode = hasRegulatorMode()
        if (hasRegulatorMode != updatedHasRegulatorMode) {
            hasRegulatorMode = updatedHasRegulatorMode
            container.initSlots()
        }
    }

    override fun renderBackground(matrixStack: MatrixStack?, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        bindTexture(RS.ID, "gui/exporter.png")
        blit(matrixStack, x, y, 0, 0, xSize, ySize)
    }

    override fun renderForeground(matrixStack: MatrixStack?, mouseX: Int, mouseY: Int) {
        renderString(matrixStack, 7, 7, title.getString())
        renderString(matrixStack, 7, 43, I18n.format("container.inventory"))
    }

    init {
        hasRegulatorMode = hasRegulatorMode()
    }
}