package com.raoulvdberge.refinedstorage.api.network;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Allows the network nodes to implement more optimal or non-regular discovery of neighbor nodes.
 */
public interface INetworkNodeVisitor {
    void visit(Operator operator);

    @FunctionalInterface
    interface Operator {
        void apply(World world, BlockPos pos, @Nullable EnumFacing side);
    }
}
