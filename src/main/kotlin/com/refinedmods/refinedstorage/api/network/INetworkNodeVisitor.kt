package com.refinedmods.refinedstorage.api.network

import com.refinedmods.refinedstorage.api.util.Action
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

/**
 * Allows the network nodes to implement more optimal or non-regular discovery of other (neighboring) nodes.
 */
interface INetworkNodeVisitor {
    /**
     * Called by the network node graph when a [Operator] has requested this node to be visited.
     *
     * @param operator the operator
     */
    fun visit(operator: Operator?)

    /**
     * An operator is passed to the [.visit] method to allow the network node visitor to add positions of nodes to scan.
     */
    interface Operator {
        /**
         * Calling this method in [.visit] will make the network graph scan the given world and position.
         * If there is another [INetworkNodeVisitor] at that position, it will call that visitor.
         * If there is no [INetworkNodeVisitor] at that position, it will use a default implementation which scans neighbors.
         *
         * @param world the world
         * @param pos   the position
         * @param side  the side
         */
        fun apply(world: World?, pos: BlockPos?, side: Direction?)

        /**
         * Returns whether the network graph is scanning in simulation mode.
         *
         * @return the action
         */
        val action: Action?
    }
}