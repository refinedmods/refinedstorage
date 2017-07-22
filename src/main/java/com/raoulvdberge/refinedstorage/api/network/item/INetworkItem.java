package com.raoulvdberge.refinedstorage.api.network.item;

import com.raoulvdberge.refinedstorage.api.network.INetwork;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

/**
 * Represents a network item (an item that is connected to the network somehow).
 * You do not implement this on the item itself, use an {@link INetworkItemProvider} for that.
 * This is an object used separately from the actual item, since this stores the player that is using it.
 */
public interface INetworkItem {
    /**
     * @return the player using the network item
     */
    EntityPlayer getPlayer();

    /**
     * Called when the network item is opened.
     *
     * @param network the network
     * @param player  the player
     * @param hand    the hand
     * @return true if the network item can be opened, false otherwise
     */
    boolean onOpen(INetwork network, EntityPlayer player, EnumHand hand);

    /**
     * Called when an action occurs that is defined in {@link NetworkItemAction} and the network item is in use.
     *
     * @param action the action
     */
    void onAction(NetworkItemAction action);
}
