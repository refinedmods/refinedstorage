package com.refinedmods.refinedstorage.screen.widget.sidebutton

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.screen.FilterScreen
import com.refinedmods.refinedstorage.tile.config.IType
import net.minecraft.client.resources.I18n
import net.minecraft.util.text.TextFormatting

class FilterTypeSideButton(screen: FilterScreen) : SideButton(screen) {
    private override val screen: FilterScreen
    override fun getTooltip(): String {
        return I18n.format("sidebutton.refinedstorage.type").toString() + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.refinedstorage.type." + screen.getType())
    }

    override fun renderButtonIcon(matrixStack: MatrixStack?, x: Int, y: Int) {
        screen.blit(matrixStack, x, y, 16 * screen.getType(), 128, 16, 16)
    }

    fun onPress() {
        screen.setType(if (screen.getType() == IType.ITEMS) IType.FLUIDS else IType.ITEMS)
        screen.sendUpdate()
    }

    init {
        this.screen = screen
    }
}