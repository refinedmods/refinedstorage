package com.refinedmods.refinedstorage.api.network



/**
 * A listener for the node graph.
 */
interface INetworkNodeGraphListener {
    /**
     * Called when the graph changes.
     */
    fun onChanged()
}