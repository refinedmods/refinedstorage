package com.refinedmods.refinedstorage.api.network.item;

import com.refinedmods.refinedstorage.inventory.player.PlayerSlot;
import net.minecraft.entity.player.PlayerEntity;
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
     * @param slot    the slot in the players inventory or curio slot, otherwise -1
     * @return the network item
     */
    @Nonnull
    INetworkItem provide(INetworkItemManager handler, PlayerEntity player, ItemStack stack, PlayerSlot slot);
}
