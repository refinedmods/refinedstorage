package com.refinedmods.refinedstorage.screen.widget.sidebutton

import com.mojang.blaze3d.matrix.MatrixStack
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist
import com.refinedmods.refinedstorage.tile.data.TileDataManager
import com.refinedmods.refinedstorage.tile.data.TileDataParameter
import net.minecraft.client.resources.I18n
import net.minecraft.util.text.TextFormatting

class WhitelistBlacklistSideButton(screen: BaseScreen<*>, private val parameter: TileDataParameter<Int?, *>) : SideButton(screen) {
    override fun getTooltip(): String {
        return I18n.format("sidebutton.refinedstorage.mode").toString() + "\n" + TextFormatting.GRAY + I18n.format("sidebutton.refinedstorage.mode." + if (parameter.value == IWhitelistBlacklist.WHITELIST) "whitelist" else "blacklist")
    }

    override fun renderButtonIcon(matrixStack: MatrixStack?, x: Int, y: Int) {
        screen.blit(matrixStack, x, y, if (parameter.value == IWhitelistBlacklist.WHITELIST) 0 else 16, 64, 16, 16)
    }

    fun onPress() {
        TileDataManager.setParameter(parameter, if (parameter.value == IWhitelistBlacklist.WHITELIST) IWhitelistBlacklist.BLACKLIST else IWhitelistBlacklist.WHITELIST)
    }
}