package com.raoulvdberge.refinedstorage.api.network.readerwriter;

import com.raoulvdberge.refinedstorage.api.network.node.INetworkNode;

/**
 * Represents a reader block in the world.
 */
public interface IReader extends INetworkNode {
    /**
     * @return the redstone strength this reader is receiving
     */
    int getRedstoneStrength();

    /**
     * @return the channel
     */
    String getChannel();

    /**
     * @param channel the channel
     */
    void setChannel(String channel);
}
