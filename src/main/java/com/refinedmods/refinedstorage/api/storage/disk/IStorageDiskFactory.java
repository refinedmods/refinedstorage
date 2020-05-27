package com.refinedmods.refinedstorage.api.storage.disk;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

/**
 * Creates a storage disk from NBT or on-demand.
 *
 * @param <T>
 */
public interface IStorageDiskFactory<T> {
    /**
     * Creates a storage disk based on NBT.
     *
     * @param world the world
     * @param tag   the tag
     * @return the storage disk
     */
    IStorageDisk<T> createFromNbt(ServerWorld world, CompoundNBT tag);

    /**
     * Creates a storage disk on-demand.
     *
     * @param world    the world
     * @param capacity the capacity
     * @return the storage disk
     */
    IStorageDisk<T> create(ServerWorld world, int capacity);
}
