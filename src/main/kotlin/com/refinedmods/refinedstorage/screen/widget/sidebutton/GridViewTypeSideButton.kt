package com.refinedmods.refinedstorage.screen.widget.sidebutton

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.api.network.grid.IGrid
import com.refinedmods.refinedstorage.container.GridContainer
import com.refinedmods.refinedstorage.screen.BaseScreen
import net.minecraft.client.resources.I18n
import net.minecraft.util.text.TextFormatting

class GridViewTypeSideButton(screen: BaseScreen<GridContainer?>, private val grid: IGrid?) : SideButton(screen) {
    override fun getTooltip(): String {
        return I18n.format("sidebutton.refinedstorage.grid.view_type").toString() + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.refinedstorage.grid.view_type." + grid!!.viewType)
    }

    override fun renderButtonIcon(matrixStack: MatrixStack?, x: Int, y: Int) {
        screen.blit(matrixStack, x, y, (grid!!.viewType - if (grid.viewType >= 3) 3 else 0) * 16, 112, 16, 16)
    }

    fun onPress() {
        var type = grid!!.viewType
        if (type == IGrid.VIEW_TYPE_NORMAL) {
            type = IGrid.VIEW_TYPE_NON_CRAFTABLES
        } else if (type == IGrid.VIEW_TYPE_NON_CRAFTABLES) {
            type = IGrid.VIEW_TYPE_CRAFTABLES
        } else if (type == IGrid.VIEW_TYPE_CRAFTABLES) {
            type = IGrid.VIEW_TYPE_NORMAL
        }
        grid.onViewTypeChanged(type)
    }
}