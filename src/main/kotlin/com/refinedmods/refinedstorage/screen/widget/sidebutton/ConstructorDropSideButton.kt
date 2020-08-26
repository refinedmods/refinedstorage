package com.refinedmods.refinedstorage.screen.widget.sidebutton

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.container.ConstructorContainer
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.tile.ConstructorTile
import com.refinedmods.refinedstorage.tile.data.TileDataManager
import net.minecraft.client.resources.I18n
import net.minecraft.util.text.TextFormatting

class ConstructorDropSideButton(screen: BaseScreen<ConstructorContainer?>) : SideButton(screen) {
    override fun renderButtonIcon(matrixStack: MatrixStack?, x: Int, y: Int) {
        screen.blit(matrixStack, x, y, 64 + if (ConstructorTile.DROP.value) 16 else 0, 16, 16, 16)
    }

    override fun getTooltip(): String {
        return I18n.format("sidebutton.refinedstorage.constructor.drop").toString() + "\n" + TextFormatting.GRAY + I18n.format(if (ConstructorTile.DROP.value) "gui.yes" else "gui.no")
    }

    fun onPress() {
        TileDataManager.setParameter(ConstructorTile.DROP, !ConstructorTile.DROP.value)
    }
}