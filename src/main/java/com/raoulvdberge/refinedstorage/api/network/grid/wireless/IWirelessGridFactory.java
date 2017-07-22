package com.raoulvdberge.refinedstorage.api.network.grid.wireless;

import com.raoulvdberge.refinedstorage.api.network.grid.IGrid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

import javax.annotation.Nonnull;

/**
 * A factory interface for a wireless grid.
 */
public interface IWirelessGridFactory {
    /**
     * Creates a new wireless grid.
     *
     * @param player           the player
     * @param hand             the hand
     * @param networkDimension the network dimension of the grid
     * @return the grid
     */
    @Nonnull
    IGrid create(EntityPlayer player, EnumHand hand, int networkDimension);
}
