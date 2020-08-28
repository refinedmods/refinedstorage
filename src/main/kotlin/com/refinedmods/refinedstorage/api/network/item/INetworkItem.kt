package com.refinedmods.refinedstorage.api.network.item

import com.refinedmods.refinedstorage.api.network.INetwork
import net.minecraft.entity.player.PlayerEntity


/**
 * Represents a network item (an item that is connected to the network somehow).
 * You do not implement this on the item itself, use an [INetworkItemProvider] for that.
 * This is an object used separately from the actual item, since this stores the player that is using it.
 */
interface INetworkItem {
    /**
     * @return the player using the network item
     */
    val player: PlayerEntity?

    /**
     * Called when the network item is being opened.
     *
     * @param network the network
     * @return true if the item can be opened, false otherwise
     */
    fun onOpen(network: INetwork?): Boolean

    /**
     * @param energy the energy to extract
     */
    fun drainEnergy(energy: Int)
}