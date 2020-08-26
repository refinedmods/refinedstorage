package com.refinedmods.refinedstorage.screen

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.RS
import com.refinedmods.refinedstorage.container.DestructorContainer
import com.refinedmods.refinedstorage.screen.widget.sidebutton.*
import com.refinedmods.refinedstorage.tile.DestructorTile
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.text.Text

class DestructorScreen(container: DestructorContainer, playerInventory: PlayerInventory?, title: Text?) : BaseScreen<DestructorContainer?>(container, 211, 137, playerInventory, title) {
    override fun onPostInit(x: Int, y: Int) {
        addSideButton(RedstoneModeSideButton(this, DestructorTile.REDSTONE_MODE))
        addSideButton(TypeSideButton(this, DestructorTile.TYPE))
        addSideButton(WhitelistBlacklistSideButton(this, DestructorTile.WHITELIST_BLACKLIST))
        addSideButton(ExactModeSideButton(this, DestructorTile.COMPARE))
        addSideButton(DestructorPickupSideButton(this))
    }

    override fun tick(x: Int, y: Int) {}
    override fun renderBackground(matrixStack: MatrixStack?, x: Int, y: Int, mouseX: Int, mouseY: Int) {
        bindTexture(RS.ID, "gui/destructor.png")
        blit(matrixStack, x, y, 0, 0, xSize, ySize)
    }

    override fun renderForeground(matrixStack: MatrixStack?, mouseX: Int, mouseY: Int) {
        renderString(matrixStack, 7, 7, title.getString())
        renderString(matrixStack, 7, 43, I18n.format("container.inventory"))
    }
}