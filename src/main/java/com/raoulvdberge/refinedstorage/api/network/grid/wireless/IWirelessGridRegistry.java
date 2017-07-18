package com.raoulvdberge.refinedstorage.api.network.grid.wireless;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

import javax.annotation.Nullable;

/**
 * A registry for wireless grid factories.
 */
public interface IWirelessGridRegistry {
    /**
     * Registers a new wireless grid factory.
     *
     * @param factory the factory
     * @return the id of this new wireless grid, use this id in {@link com.raoulvdberge.refinedstorage.api.IRSAPI#openWirelessGrid(EntityPlayer, EnumHand, int, int)}.
     */
    int add(IWirelessGridFactory factory);

    /**
     * Gets a wireless grid factory by id.
     *
     * @param id the id, as returned by {@link #add(IWirelessGridFactory)}
     * @return the wireless grid factory, or null if none is found
     */
    @Nullable
    IWirelessGridFactory get(int id);
}
