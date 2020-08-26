package com.refinedmods.refinedstorage.container

import com.refinedmods.refinedstorage.RSContainers
import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.tile.ControllerTile
import net.minecraft.entity.player.PlayerEntity

class ControllerContainer(controller: ControllerTile?, player: PlayerEntity, windowId: Int) : BaseContainer(RSContainers.CONTROLLER, controller, player, windowId) {
    init {
        addPlayerInventory(8, 99)
    }
}