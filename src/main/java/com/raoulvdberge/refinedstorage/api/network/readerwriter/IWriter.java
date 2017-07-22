package com.raoulvdberge.refinedstorage.api.network.readerwriter;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;

import net.minecraft.util.EnumFacing;

/**
 * Represents a writer network node.
 */
public interface IWriter extends INetworkNode {
    /**
     * @return the redstone strength that this writer is emitting
     */
    int getRedstoneStrength();

    /**
     * @param strength the redstone strength to be emitted
     */
    void setRedstoneStrength(int strength);

    /**
     * @return the direction of the writer
     */
    EnumFacing getDirection();

    /**
     * @return the channel
     */
    String getChannel();

    /**
     * @param channel the channel
     */
    void setChannel(String channel);
}
