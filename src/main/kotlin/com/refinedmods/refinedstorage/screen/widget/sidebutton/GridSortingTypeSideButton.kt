package com.refinedmods.refinedstorage.screen.widget.sidebutton

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.api.network.grid.GridType
import com.refinedmods.refinedstorage.api.network.grid.IGrid
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode
import com.refinedmods.refinedstorage.container.GridContainer
import com.refinedmods.refinedstorage.integration.inventorytweaks.InventoryTweaksIntegration
import com.refinedmods.refinedstorage.screen.BaseScreen
import net.minecraft.client.resources.I18n
import net.minecraft.util.text.TextFormatting

class GridSortingTypeSideButton(screen: BaseScreen<GridContainer?>, private val grid: IGrid?) : SideButton(screen) {
    override fun getTooltip(): String {
        return I18n.format("sidebutton.refinedstorage.grid.sorting.type").toString() + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.refinedstorage.grid.sorting.type." + grid!!.sortingType)
    }

    override fun renderButtonIcon(matrixStack: MatrixStack?, x: Int, y: Int) {
        if (grid!!.sortingType == IGrid.SORTING_TYPE_LAST_MODIFIED) {
            screen.blit(matrixStack, x, y, 48, 48, 16, 16)
        } else {
            screen.blit(matrixStack, x, y, grid.sortingType * 16, 32, 16, 16)
        }
    }

    fun onPress() {
        var type = grid!!.sortingType
        if (type == IGrid.SORTING_TYPE_QUANTITY) {
            type = IGrid.SORTING_TYPE_NAME
        } else if (type == IGrid.SORTING_TYPE_NAME) {
            type = if (grid.gridType === GridType.FLUID) {
                IGrid.SORTING_TYPE_LAST_MODIFIED
            } else {
                IGrid.SORTING_TYPE_ID
            }
        } else if (type == IGrid.SORTING_TYPE_ID) {
            type = IGrid.SORTING_TYPE_LAST_MODIFIED
        } else if (type == GridNetworkNode.SORTING_TYPE_LAST_MODIFIED) {
            type = if (grid.gridType === GridType.FLUID || !InventoryTweaksIntegration.isLoaded) {
                IGrid.SORTING_TYPE_QUANTITY
            } else {
                IGrid.SORTING_TYPE_INVENTORYTWEAKS
            }
        } else if (type == GridNetworkNode.SORTING_TYPE_INVENTORYTWEAKS) {
            type = IGrid.SORTING_TYPE_QUANTITY
        }
        grid.onSortingTypeChanged(type)
    }
}