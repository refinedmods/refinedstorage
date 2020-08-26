package com.refinedmods.refinedstorage.screen.widget.sidebutton

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.container.CrafterContainer
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.tile.CrafterTile
import com.refinedmods.refinedstorage.tile.data.TileDataManager
import net.minecraft.client.resources.I18n
import net.minecraft.util.text.TextFormatting

class CrafterModeSideButton(screen: BaseScreen<CrafterContainer?>) : SideButton(screen) {
    override fun getTooltip(): String {
        return I18n.format("sidebutton.refinedstorage.crafter_mode").toString() + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.refinedstorage.crafter_mode." + CrafterTile.MODE.value)
    }

    override fun renderButtonIcon(matrixStack: MatrixStack?, x: Int, y: Int) {
        screen.blit(matrixStack, x, y, CrafterTile.MODE.value * 16, 0, 16, 16)
    }

    fun onPress() {
        TileDataManager.setParameter(CrafterTile.MODE, CrafterTile.MODE.value + 1)
    }
}