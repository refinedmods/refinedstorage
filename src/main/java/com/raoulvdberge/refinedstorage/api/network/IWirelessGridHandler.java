package com.raoulvdberge.refinedstorage.api.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Defines behavior of wireless grids.
 */
public interface IWirelessGridHandler {
    /**
     * Called every network tick.
     */
    void update();

    /**
     * Called when a player opens a wireless grid.
     *
     * @param player          the player that opened the wireless grid
     * @param controllerWorld the world of the controller
     * @param hand            the hand the player opened it with
     * @return true if the opening was successful, false otherwise
     */
    boolean onOpen(EntityPlayer player, World controllerWorld, EnumHand hand);

    /**
     * Called when the player closes a wireless grid.
     *
     * @param player the player that closed the grid
     */
    void onClose(EntityPlayer player);

    /**
     * Drains energy from the wireless grid of a player.
     *
     * @param player the player that is using the wireless grid to drain energy from
     * @param energy the amount of energy that has to be drained
     */
    void drainEnergy(EntityPlayer player, int energy);

    /**
     * Returns a {@link IWirelessGridConsumer} for a player.
     *
     * @param player the player to get the wireless grid consumer for
     * @return the {@link IWirelessGridConsumer} that corresponds to a player, or null if the player isn't using a wireless grid
     */
    @Nullable
    IWirelessGridConsumer getConsumer(EntityPlayer player);
}
