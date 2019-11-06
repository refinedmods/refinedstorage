package com.raoulvdberge.refinedstorage.api.network.node;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * A factory for reading network nodes from the disk. Used in a {@link INetworkNodeRegistry}.
 */
public interface INetworkNodeFactory {
    /**
     * Creates a network node.
     *
     * @param tag   the tag on disk
     * @param world the world
     * @param pos   the pos
     * @return the network node
     */
    @Nonnull
    INetworkNode create(CompoundNBT tag, World world, BlockPos pos);
}
