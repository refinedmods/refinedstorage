package com.raoulvdberge.refinedstorage.api.network.item;

import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

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
     * @param network         the network
     * @param player          the player
     * @param controllerWorld the world where the controller is in
     * @param hand            the hand
     * @return true if the network item can be opened, false otherwise
     */
    boolean onOpen(INetworkMaster network, EntityPlayer player, World controllerWorld, EnumHand hand);
}
