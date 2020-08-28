package com.refinedmods.refinedstorage.api.network

import com.refinedmods.refinedstorage.api.network.node.INetworkNode
import com.refinedmods.refinedstorage.api.util.Action
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.function.Consumer


/**
 * Represents a graph of all the nodes connected to a network.
 */
interface INetworkNodeGraph {
    /**
     * Rebuilds the network graph.
     *
     * @param action whether to perform or simulate
     * @param world  the origin world
     * @param origin the origin, usually the network position
     */
    fun invalidate(action: Action?, world: World?, origin: BlockPos?)

    /**
     * Runs an action on the network.
     * If the network is rebuilding it's graph, the action will be executed after the graph was built.
     *
     * @param handler the action to run
     */
    fun runActionWhenPossible(handler: Consumer<INetwork?>?)

    /**
     * @return a collection of all connected nodes
     */
    fun all(): Collection<INetworkNode?>?

    /**
     * @param listener the listener
     */
    fun addListener(listener: INetworkNodeGraphListener?)

    /**
     * Disconnects and notifies all connected nodes.
     */
    fun disconnectAll()
}