package com.refinedmods.refinedstorage.api.network.node

/**
 * Makes a network node accessible from a tile entity. Implement this as a capability.
 *
 * @param <T> the network node
</T> */
interface INetworkNodeProxy<T : INetworkNode> {
    /**
     * Returns the node.
     * Needs to work on the client and the server.
     * If there is no node present, don't silently return null but throw an exception since the game is in a bad state if that happens.
     *
     * @return the node
     */
    val node: T
}