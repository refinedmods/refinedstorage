package com.refinedmods.refinedstorage.container

import com.refinedmods.refinedstorage.RSContainers
import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.tile.NetworkTransmitterTile
import net.minecraft.entity.player.PlayerEntity
import net.minecraftforge.items.SlotItemHandler

class NetworkTransmitterContainer(networkTransmitter: NetworkTransmitterTile, player: PlayerEntity, windowId: Int) : BaseContainer(RSContainers.NETWORK_TRANSMITTER, networkTransmitter, player, windowId) {
    init {
        addSlot(SlotItemHandler(networkTransmitter.getNode().getNetworkCard(), 0, 8, 20))
        addPlayerInventory(8, 55)
        transferManager.addBiTransfer(player.inventory, networkTransmitter.getNode().getNetworkCard())
    }
}