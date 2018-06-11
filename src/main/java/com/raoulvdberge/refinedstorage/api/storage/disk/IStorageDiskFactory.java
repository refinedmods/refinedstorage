package com.raoulvdberge.refinedstorage.api.storage.disk;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

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
    IStorageDisk<T> createFromNbt(World world, NBTTagCompound tag);

    /**
     * Creates a storage disk on-demand.
     *
     * @param world    the world
     * @param capacity the capacity
     * @return the storage disk
     */
    IStorageDisk<T> create(World world, int capacity);
}
