package com.refinedmods.refinedstorage.screen.widget.sidebutton

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.tile.config.IType
import com.refinedmods.refinedstorage.tile.data.TileDataManager
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.client.resources.I18n
import net.minecraft.util.text.TextFormatting

class TypeSideButton(screen: BaseScreen<*>, private val type: TileDataParameter<Int?, *>) : SideButton(screen) {
    override fun getTooltip(): String {
        return I18n.format("sidebutton.refinedstorage.type").toString() + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.refinedstorage.type." + type.value)
    }

    override fun renderButtonIcon(matrixStack: MatrixStack?, x: Int, y: Int) {
        screen.blit(matrixStack, x, y, 16 * type.value!!, 128, 16, 16)
    }

    fun onPress() {
        TileDataManager.setParameter(type, if (type.value == IType.ITEMS) IType.FLUIDS else IType.ITEMS)
    }
}