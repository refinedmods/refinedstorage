package com.refinedmods.refinedstorage.screen.widget.sidebutton

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.apiimpl.network.node.DetectorNetworkNode
import com.refinedmods.refinedstorage.container.DetectorContainer
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.tile.DetectorTile
import com.refinedmods.refinedstorage.tile.data.TileDataManager
import net.minecraft.client.resources.I18n
import net.minecraft.util.text.TextFormatting

class DetectorModeSideButton(screen: BaseScreen<DetectorContainer?>) : SideButton(screen) {
    override fun getTooltip(): String {
        return I18n.format("sidebutton.refinedstorage.detector.mode").toString() + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.refinedstorage.detector.mode." + DetectorTile.MODE.value)
    }

    override fun renderButtonIcon(matrixStack: MatrixStack?, x: Int, y: Int) {
        screen.blit(matrixStack, x, y, DetectorTile.MODE.value * 16, 176, 16, 16)
    }

    fun onPress() {
        var mode = DetectorTile.MODE.value
        if (mode == DetectorNetworkNode.MODE_EQUAL) {
            mode = DetectorNetworkNode.MODE_ABOVE
        } else if (mode == DetectorNetworkNode.MODE_ABOVE) {
            mode = DetectorNetworkNode.MODE_UNDER
        } else if (mode == DetectorNetworkNode.MODE_UNDER) {
            mode = DetectorNetworkNode.MODE_EQUAL
        }
        TileDataManager.setParameter(DetectorTile.MODE, mode)
    }
}