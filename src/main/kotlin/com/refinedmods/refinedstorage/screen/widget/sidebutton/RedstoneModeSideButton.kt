package com.refinedmods.refinedstorage.screen.widget.sidebutton

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.tile.data.TileDataManager
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.client.resources.I18n
import net.minecraft.util.text.TextFormatting

class RedstoneModeSideButton(screen: BaseScreen<*>, private val parameter: TileDataParameter<Int?, *>) : SideButton(screen) {
    override fun getTooltip(): String {
        return I18n.format("sidebutton.refinedstorage.redstone_mode").toString() + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.refinedstorage.redstone_mode." + parameter.value)
    }

    override fun renderButtonIcon(matrixStack: MatrixStack?, x: Int, y: Int) {
        screen.blit(matrixStack, x, y, parameter.value!! * 16, 0, 16, 16)
    }

    fun onPress() {
        TileDataManager.setParameter(parameter, parameter.value!! + 1)
    }
}