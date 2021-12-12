package com.refinedmods.refinedstorage.api.storage.disk;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.UUID;

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
    IStorageDisk<T> createFromNbt(ServerLevel world, CompoundTag tag);

    /**
     * Creates a storage disk item based on ID.
     *
     * @param disk the disk
     * @param id   the disk id
     * @return the storage disk
     */
    ItemStack createDiskItem(IStorageDisk<T> disk, UUID id);

    /**
     * Creates a storage disk on-demand.
     *
     * @param world    the world
     * @param capacity the capacity
     * @param owner    the owner, or null if no owner
     * @return the storage disk
     */
    IStorageDisk<T> create(ServerLevel world, int capacity, @Nullable UUID owner);
}
