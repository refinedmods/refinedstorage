package com.raoulvdberge.refinedstorage.api.network;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

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
    DimensionType getDimension();
}
