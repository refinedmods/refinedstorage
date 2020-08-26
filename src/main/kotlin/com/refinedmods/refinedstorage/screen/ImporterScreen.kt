package com.refinedmods.refinedstorage.screen

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.container.ImporterContainer
import com.refinedmods.refinedstorage.screen.widget.sidebutton.ExactModeSideButton
import com.refinedmods.refinedstorage.screen.widget.sidebutton.RedstoneModeSideButton
import com.refinedmods.refinedstorage.screen.widget.sidebutton.TypeSideButton
import com.refinedmods.refinedstorage.screen.widget.sidebutton.WhitelistBlacklistSideButton
import com.refinedmods.refinedstorage.tile.ImporterTile
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.text.Text

class ImporterScreen(container: ImporterContainer, inventory: PlayerInventory?, title: Text?) : BaseScreen<ImporterContainer?>(container, 211, 137, inventory, title) {
    override fun onPostInit(x: Int, y: Int) {
        addSideButton(RedstoneModeSideButton(this, ImporterTile.REDSTONE_MODE))
        addSideButton(TypeSideButton(this, ImporterTile.TYPE))
        addSideButton(WhitelistBlacklistSideButton(this, ImporterTile.WHITELIST_BLACKLIST))
        addSideButton(ExactModeSideButton(this, ImporterTile.COMPARE))
    }

    override fun tick(x: Int, y: Int) {}
    override fun renderBackground(matrixStack: MatrixStack?, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        bindTexture(RS.ID, "gui/importer.png")
        blit(matrixStack, x, y, 0, 0, xSize, ySize)
    }

    override fun renderForeground(matrixStack: MatrixStack?, mouseX: Int, mouseY: Int) {
        renderString(matrixStack, 7, 7, title.getString())
        renderString(matrixStack, 7, 43, I18n.format("container.inventory"))
    }
}