package com.refinedmods.refinedstorage.api.network.grid

import com.refinedmods.refinedstorage.api.network.INetwork


/**
 * A grid that knows about a network.
 */
interface INetworkAwareGrid : IGrid {
    /**
     * @return the network, or null if no network is available
     */
    val network: INetwork?
}