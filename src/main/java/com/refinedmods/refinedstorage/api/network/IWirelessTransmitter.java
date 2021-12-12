package com.refinedmods.refinedstorage.api.network;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

/**
 * Represents a node that can send a wireless signal.
 */
public interface IWirelessTransmitter {
    /**
     * @return the range in blocks of this transmitter, starting from {@link IWirelessTransmitter#getOrigin()}
     */
    int getRange();

    /**
     * @return the position where the wireless signal starts
     */
    BlockPos getOrigin();

    /**
     * @return the dimension in which the transmitter is
     */
    ResourceKey<Level> getDimension();
}
