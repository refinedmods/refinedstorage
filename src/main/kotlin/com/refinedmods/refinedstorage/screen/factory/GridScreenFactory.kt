package com.refinedmods.refinedstorage.screen.factory

import com.refinedmods.refinedstorage.container.GridContainer
import com.refinedmods.refinedstorage.screen.grid.GridScreen
import net.minecraft.client.gui.ScreenManager
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.util.text.Text

class GridScreenFactory : ScreenManager.IScreenFactory<GridContainer?, GridScreen?> {
    fun create(container: GridContainer, inv: PlayerInventory?, title: Text?): GridScreen {
        val screen = GridScreen(container, container.grid, inv, title)
        container.setScreenInfoProvider(screen)
        return screen
    }
}