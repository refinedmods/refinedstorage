package com.refinedmods.refinedstorage.screen.widget.sidebutton

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.api.util.IComparer
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.tile.data.TileDataManager
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.client.resources.I18n
import net.minecraft.util.text.TextFormatting

class ExactModeSideButton(screen: BaseScreen<*>, private val parameter: TileDataParameter<Int?, *>) : SideButton(screen) {
    override fun getTooltip(): String {
        var tooltip: String = I18n.format("sidebutton.refinedstorage.exact_mode").toString() + "\n" + TextFormatting.GRAY
        if (parameter.value!! and MASK == MASK) {
            tooltip += I18n.format("sidebutton.refinedstorage.exact_mode.on")
        } else {
            tooltip += I18n.format("sidebutton.refinedstorage.exact_mode.off")
        }
        return tooltip
    }

    override fun renderButtonIcon(matrixStack: MatrixStack?, x: Int, y: Int) {
        val ty = 16 * 12
        val tx = if (parameter.value!! and MASK == MASK) 0 else 16
        screen.blit(matrixStack, x, y, tx, ty, 16, 16)
    }

    fun onPress() {
        TileDataManager.setParameter(parameter, parameter.value!! xor MASK)
    }

    companion object {
        private const val MASK = IComparer.COMPARE_NBT
    }
}