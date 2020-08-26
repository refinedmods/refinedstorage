package com.refinedmods.refinedstorage.screen

import com.refinedmods.refinedstorage.screen.widget.sidebutton.CrafterModeSideButton
import com.refinedmods.refinedstorage.tile.data.TileDataParameterClientListener
import java.util.function.Consumer

class CrafterTileDataParameterClientListener : TileDataParameterClientListener<Boolean?> {
    override fun onChanged(initial: Boolean, hasRoot: Boolean) {
        if (!hasRoot) {
            BaseScreen.Companion.executeLater(CrafterScreen::class.java, Consumer { gui: CrafterScreen -> gui.addSideButton(CrafterModeSideButton(gui)) })
        }
    }
}