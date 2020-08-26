package com.refinedmods.refinedstorage.screen.widget.sidebutton

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.api.storage.AccessType
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.tile.data.TileDataManager
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import com.refinedmods.refinedstorage.util.AccessTypeUtils
import net.minecraft.client.resources.I18n
import net.minecraft.util.text.TextFormatting

class AccessTypeSideButton(screen: BaseScreen<*>, private val parameter: TileDataParameter<AccessType?, *>) : SideButton(screen) {
    override fun renderButtonIcon(matrixStack: MatrixStack?, x: Int, y: Int) {
        screen.blit(matrixStack, x, y, 16 * parameter.value!!.getId(), 240, 16, 16)
    }

    override fun getTooltip(): String {
        return I18n.format("sidebutton.refinedstorage.access_type").toString() + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.refinedstorage.access_type." + parameter.value!!.getId())
    }

    fun onPress() {
        TileDataManager.setParameter(parameter, AccessTypeUtils.getAccessType(parameter.value!!.getId() + 1))
    }
}