package com.refinedmods.refinedstorage.screen

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.container.DiskManipulatorContainer
import com.refinedmods.refinedstorage.screen.widget.sidebutton.*
import com.refinedmods.refinedstorage.tile.DiskManipulatorTile
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.text.Text

class DiskManipulatorScreen(container: DiskManipulatorContainer, playerInventory: PlayerInventory?, title: Text?) : BaseScreen<DiskManipulatorContainer?>(container, 211, 211, playerInventory, title) {
    override fun onPostInit(x: Int, y: Int) {
        addSideButton(RedstoneModeSideButton(this, DiskManipulatorTile.REDSTONE_MODE))
        addSideButton(IoModeSideButton(this))
        addSideButton(TypeSideButton(this, DiskManipulatorTile.TYPE))
        addSideButton(WhitelistBlacklistSideButton(this, DiskManipulatorTile.WHITELIST_BLACKLIST))
        addSideButton(ExactModeSideButton(this, DiskManipulatorTile.COMPARE))
    }

    override fun tick(x: Int, y: Int) {}
    override fun renderBackground(matrixStack: MatrixStack?, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        bindTexture(RS.ID, "gui/disk_manipulator.png")
        blit(matrixStack, x, y, 0, 0, xSize, ySize)
    }

    override fun renderForeground(matrixStack: MatrixStack?, mouseX: Int, mouseY: Int) {
        renderString(matrixStack, 7, 7, title.getString())
        renderString(matrixStack, 7, 117, I18n.format("container.inventory"))
        renderString(matrixStack, 43, 45, I18n.format("gui.refinedstorage.disk_manipulator.in"))
        renderString(matrixStack, 115, 45, I18n.format("gui.refinedstorage.disk_manipulator.out"))
    }
}