package com.refinedmods.refinedstorage.screen.factory

import com.refinedmods.refinedstorage.container.CrafterManagerContainer
import com.refinedmods.refinedstorage.screen.CrafterManagerScreen
import net.minecraft.client.gui.ScreenManager
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.text.Text

class CrafterManagerScreenFactory : ScreenManager.IScreenFactory<CrafterManagerContainer?, CrafterManagerScreen?> {
    fun create(container: CrafterManagerContainer, playerInventory: PlayerInventory?, title: Text?): CrafterManagerScreen {
        val screen = CrafterManagerScreen(container, playerInventory, title)
        container.setScreenInfoProvider(screen)
        return screen
    }
}