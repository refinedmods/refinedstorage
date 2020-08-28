package com.refinedmods.refinedstorage.api.network.item

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack


/**
 * Provider for network items, implement this on the item.
 */
interface INetworkItemProvider {
    /**
     * Creates a network item for the given item stack.
     *
     * @param handler the network item handler
     * @param player  the player
     * @param stack   the stack
     * @param slotId  the slot id, if applicable, otherwise -1
     * @return the network item
     */
    fun provide(handler: INetworkItemManager, player: PlayerEntity, stack: ItemStack, slotId: Int): INetworkItem
}