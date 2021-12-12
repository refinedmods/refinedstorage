package com.refinedmods.refinedstorage.api.network;

import com.refinedmods.refinedstorage.api.util.Action;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

/**
 * Allows the network nodes to implement more optimal or non-regular discovery of other (neighboring) nodes.
 */
public interface INetworkNodeVisitor {
    /**
     * Called by the network node graph when a {@link Operator} has requested this node to be visited.
     *
     * @param operator the operator
     */
    void visit(Operator operator);

    /**
     * An operator is passed to the {@link #visit(Operator)} method to allow the network node visitor to add positions of nodes to scan.
     */
    interface Operator {
        /**
         * Calling this method in {@link #visit(Operator)} will make the network graph scan the given world and position.
         * If there is another {@link INetworkNodeVisitor} at that position, it will call that visitor.
         * If there is no {@link INetworkNodeVisitor} at that position, it will use a default implementation which scans neighbors.
         *
         * @param world the world
         * @param pos   the position
         * @param side  the side
         */
        void apply(Level world, BlockPos pos, @Nullable Direction side);

        /**
         * Returns whether the network graph is scanning in simulation mode.
         *
         * @return the action
         */
        Action getAction();
    }
}
