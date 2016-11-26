package com.raoulvdberge.refinedstorage.api.network;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Allow the network nodes to implement more optimal or non-regular discovery of neighbor nodes
 */
public interface INetworkNeighborhoodAware {
    void walkNeighborhood(Operator operator);

    @FunctionalInterface
    interface Operator {
        void apply(World world, BlockPos pos, EnumFacing side);
    }

}
