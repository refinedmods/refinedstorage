package com.refinedmods.refinedstorage.api.network.item;

import com.refinedmods.refinedstorage.api.network.INetwork;
import net.minecraft.world.entity.player.Player;

/**
 * Represents a network item (an item that is connected to the network somehow).
 * You do not implement this on the item itself, use an {@link INetworkItemProvider} for that.
 * This is an object used separately from the actual item, since this stores the player that is using it.
 */
public interface INetworkItem {
    /**
     * @return the player using the network item
     */
    Player getPlayer();

    /**
     * Called when the network item is being opened.
     *
     * @param network the network
     * @return true if the item can be opened, false otherwise
     */
    boolean onOpen(INetwork network);

    /**
     * @param energy the energy to extract
     */
    void drainEnergy(int energy);
}
