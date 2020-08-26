package com.refinedmods.refinedstorage.container

import com.refinedmods.refinedstorage.RSContainers
import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.tile.WirelessTransmitterTile
import net.minecraft.entity.player.PlayerEntity
import net.minecraftforge.items.SlotItemHandler

class WirelessTransmitterContainer(wirelessTransmitter: WirelessTransmitterTile, player: PlayerEntity, windowId: Int) : BaseContainer(RSContainers.WIRELESS_TRANSMITTER, wirelessTransmitter, player, windowId) {
    init {
        for (i in 0..3) {
            addSlot(SlotItemHandler(wirelessTransmitter.getNode().getUpgrades(), i, 187, 6 + i * 18))
        }
        addPlayerInventory(8, 55)
        transferManager.addBiTransfer(player.inventory, wirelessTransmitter.getNode().getUpgrades())
    }
}