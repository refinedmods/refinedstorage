package com.refinedmods.refinedstorage.screen.widget.sidebutton

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.api.network.grid.IGrid
import com.refinedmods.refinedstorage.container.GridContainer
import com.refinedmods.refinedstorage.screen.BaseScreen
import net.minecraft.client.resources.I18n
import net.minecraft.util.text.TextFormatting

class GridSortingDirectionSideButton(screen: BaseScreen<GridContainer?>, private val grid: IGrid?) : SideButton(screen) {
    override fun getTooltip(): String {
        return I18n.format("sidebutton.refinedstorage.grid.sorting.direction").toString() + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.refinedstorage.grid.sorting.direction." + grid!!.sortingDirection)
    }

    override fun renderButtonIcon(matrixStack: MatrixStack?, x: Int, y: Int) {
        screen.blit(matrixStack, x, y, grid!!.sortingDirection * 16, 16, 16, 16)
    }

    fun onPress() {
        var dir = grid!!.sortingDirection
        if (dir == IGrid.SORTING_DIRECTION_ASCENDING) {
            dir = IGrid.SORTING_DIRECTION_DESCENDING
        } else if (dir == IGrid.SORTING_DIRECTION_DESCENDING) {
            dir = IGrid.SORTING_DIRECTION_ASCENDING
        }
        grid.onSortingDirectionChanged(dir)
    }
}