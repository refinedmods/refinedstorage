package com.raoulvdberge.refinedstorage.api.network.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Provider for network items, implement this on the item.
 */
public interface INetworkItemProvider {
    /**
     * Creates a network item for the given item stack.
     *
     * @param handler the network item handler
     * @param player  the player
     * @param stack   the stack
     * @return the network item
     */
    @Nonnull
    INetworkItem provide(INetworkItemHandler handler, EntityPlayer player, ItemStack stack);
}
