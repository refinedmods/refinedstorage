package com.raoulvdberge.refinedstorage.api.network.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * This is the handler for network items of a network.
 * It stores which player is currently using what network item.
 */
public interface INetworkItemHandler {
    /**
     * Called every network tick.
     */
    void update();

    /**
     * Called when a player opens a network item.
     *
     * @param player          the player that opened the network item
     * @param controllerWorld the world of the controller
     * @param hand            the hand the player opened it with
     */
    void onOpen(EntityPlayer player, World controllerWorld, EnumHand hand);

    /**
     * Called when the player closes a network item.
     *
     * @param player the player that closed the network item
     */
    void onClose(EntityPlayer player);

    /**
     * Returns a {@link INetworkItem} for a player.
     *
     * @param player the player to get the network item for
     * @return the {@link INetworkItem} that corresponds to a player, or null if the player isn't using a network item
     */
    @Nullable
    INetworkItem getItem(EntityPlayer player);
}
