package com.refinedmods.refinedstorage.api.network.item;

import com.refinedmods.refinedstorage.inventory.player.PlayerSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * This is the handler for network items of a network.
 * It stores which player is currently using what network item.
 */
public interface INetworkItemManager {
    /**
     * Called when a player opens a network item.
     *
     * @param player the player that opened the network item
     * @param stack  the stack that has been opened
     * @param slot   the slot in the players inventory or curio slot, otherwise -1
     */
    void open(PlayerEntity player, ItemStack stack, PlayerSlot slot);

    /**
     * Called when the player closes a network item.
     *
     * @param player the player that closed the network item
     */
    void close(PlayerEntity player);

    /**
     * Returns a {@link INetworkItem} for a player.
     *
     * @param player the player to get the network item for
     * @return the {@link INetworkItem} that corresponds to a player, or null if the player isn't using a network item
     */
    @Nullable
    INetworkItem getItem(PlayerEntity player);

    /**
     * @param player the player
     * @param energy energy to extract
     */
    void drainEnergy(PlayerEntity player, int energy);
}
