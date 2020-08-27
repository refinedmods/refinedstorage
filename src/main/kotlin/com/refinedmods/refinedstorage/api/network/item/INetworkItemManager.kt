package com.refinedmods.refinedstorage.api.network.item

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack


/**
 * This is the handler for network items of a network.
 * It stores which player is currently using what network item.
 */
interface INetworkItemManager {
    /**
     * Called when a player opens a network item.
     *
     * @param player the player that opened the network item
     * @param stack  the stack that has been opened
     * @param slotId the slot id, if applicable, otherwise -1
     */
    fun open(player: PlayerEntity?, stack: ItemStack?, slotId: Int)

    /**
     * Called when the player closes a network item.
     *
     * @param player the player that closed the network item
     */
    fun close(player: PlayerEntity?)

    /**
     * Returns a [INetworkItem] for a player.
     *
     * @param player the player to get the network item for
     * @return the [INetworkItem] that corresponds to a player, or null if the player isn't using a network item
     */
    fun getItem(player: PlayerEntity): INetworkItem?

    /**
     * @param player the player
     * @param energy energy to extract
     */
    fun drainEnergy(player: PlayerEntity?, energy: Int)
}