package com.refinedmods.refinedstorage.screen.widget.sidebutton

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.container.DestructorContainer
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.tile.DestructorTile
import com.refinedmods.refinedstorage.tile.data.TileDataManager
import net.minecraft.client.resources.I18n
import net.minecraft.util.text.TextFormatting

class DestructorPickupSideButton(screen: BaseScreen<DestructorContainer?>) : SideButton(screen) {
    override fun renderButtonIcon(matrixStack: MatrixStack?, x: Int, y: Int) {
        screen.blit(matrixStack, x, y, 64 + if (!DestructorTile.PICKUP.value) 16 else 0, 0, 16, 16)
    }

    override fun getTooltip(): String {
        return I18n.format("sidebutton.refinedstorage.destructor.pickup").toString() + "\n" + TextFormatting.GRAY + I18n.format(if (DestructorTile.PICKUP.value) "gui.yes" else "gui.no")
    }

    fun onPress() {
        TileDataManager.setParameter(DestructorTile.PICKUP, !DestructorTile.PICKUP.value)
    }
}