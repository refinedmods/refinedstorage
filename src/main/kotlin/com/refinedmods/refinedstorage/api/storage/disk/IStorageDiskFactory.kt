package com.refinedmods.refinedstorage.api.storage.disk

import net.minecraft.nbt.CompoundTag
import net.minecraft.server.world.ServerWorld

/**
 * Creates a storage disk from NBT or on-demand.
 *
 * @param <T>
</T> */
interface IStorageDiskFactory<T> {
    /**
     * Creates a storage disk based on NBT.
     *
     * @param world the world
     * @param tag   the tag
     * @return the storage disk
     */
    fun createFromNbt(world: ServerWorld, tag: CompoundTag): IStorageDisk<T>

    /**
     * Creates a storage disk on-demand.
     *
     * @param world    the world
     * @param capacity the capacity
     * @return the storage disk
     */
    fun create(world: ServerWorld, capacity: Int): IStorageDisk<T>
}