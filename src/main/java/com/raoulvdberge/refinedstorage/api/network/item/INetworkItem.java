package com.raoulvdberge.refinedstorage.api.network.item;

import com.raoulvdberge.refinedstorage.api.network.INetworkMaster;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

/**
 * Represents a network item.
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
     * @return whether the network item can be opened
     */
    boolean onOpen(INetworkMaster network, EntityPlayer player, World controllerWorld, EnumHand hand);
}
