package com.refinedmods.refinedstorage.container

import com.refinedmods.refinedstorage.RSContainers
import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.tile.RelayTile
import net.minecraft.entity.player.PlayerEntity

class RelayContainer(relay: RelayTile?, player: PlayerEntity, windowId: Int) : BaseContainer(RSContainers.RELAY, relay, player, windowId) {
    init {
        addPlayerInventory(8, 50)
    }
}