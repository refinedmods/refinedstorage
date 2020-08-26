package com.refinedmods.refinedstorage.screen.widget.sidebutton

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.apiimpl.network.node.diskmanipulator.DiskManipulatorNetworkNode
import com.refinedmods.refinedstorage.container.DiskManipulatorContainer
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.tile.DiskManipulatorTile
import com.refinedmods.refinedstorage.tile.data.TileDataManager
import net.minecraft.client.resources.I18n
import net.minecraft.util.text.TextFormatting

class IoModeSideButton(screen: BaseScreen<DiskManipulatorContainer?>) : SideButton(screen) {
    override fun getTooltip(): String {
        return I18n.format("sidebutton.refinedstorage.iomode").toString() + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.refinedstorage.iomode." + if (DiskManipulatorTile.IO_MODE.value == DiskManipulatorNetworkNode.IO_MODE_INSERT) "insert" else "extract")
    }

    override fun renderButtonIcon(matrixStack: MatrixStack?, x: Int, y: Int) {
        screen.blit(matrixStack, x, y, if (DiskManipulatorTile.IO_MODE.value == DiskManipulatorNetworkNode.IO_MODE_EXTRACT) 0 else 16, 160, 16, 16)
    }

    fun onPress() {
        TileDataManager.setParameter(DiskManipulatorTile.IO_MODE, if (DiskManipulatorTile.IO_MODE.value == DiskManipulatorNetworkNode.IO_MODE_INSERT) DiskManipulatorNetworkNode.IO_MODE_EXTRACT else DiskManipulatorNetworkNode.IO_MODE_INSERT)
    }
}