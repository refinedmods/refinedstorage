package com.refinedmods.refinedstorage.screen.widget.sidebutton

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.api.network.grid.IGrid
import com.refinedmods.refinedstorage.screen.BaseScreen
import net.minecraft.client.resources.I18n
import net.minecraft.util.text.TextFormatting
import java.util.function.Consumer
import java.util.function.Supplier

class GridSizeSideButton(screen: BaseScreen<*>, private val sizeSupplier: Supplier<Int>, private val listener: Consumer<Int?>) : SideButton(screen) {
    override fun getTooltip(): String {
        return I18n.format("sidebutton.refinedstorage.grid.size").toString() + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.refinedstorage.grid.size." + sizeSupplier.get())
    }

    override fun renderButtonIcon(matrixStack: MatrixStack?, x: Int, y: Int) {
        val size = sizeSupplier.get()
        var tx = 0
        if (size == IGrid.SIZE_STRETCH) {
            tx = 48
        } else if (size == IGrid.SIZE_SMALL) {
            tx = 0
        } else if (size == IGrid.SIZE_MEDIUM) {
            tx = 16
        } else if (size == IGrid.SIZE_LARGE) {
            tx = 32
        }
        screen.blit(matrixStack, x, y, 64 + tx, 64, 16, 16)
    }

    fun onPress() {
        var size = sizeSupplier.get()
        if (size == IGrid.SIZE_STRETCH) {
            size = IGrid.SIZE_SMALL
        } else if (size == IGrid.SIZE_SMALL) {
            size = IGrid.SIZE_MEDIUM
        } else if (size == IGrid.SIZE_MEDIUM) {
            size = IGrid.SIZE_LARGE
        } else if (size == IGrid.SIZE_LARGE) {
            size = IGrid.SIZE_STRETCH
        }
        listener.accept(size)
    }
}