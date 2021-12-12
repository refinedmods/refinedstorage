package com.refinedmods.refinedstorage.api.network.node;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

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
    INetworkNode create(CompoundTag tag, Level world, BlockPos pos);
}
