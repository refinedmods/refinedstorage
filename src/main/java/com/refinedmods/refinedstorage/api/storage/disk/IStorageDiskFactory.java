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
     * @param level the level
     * @param tag   the tag
     * @return the storage disk
     */
    IStorageDisk<T> createFromNbt(ServerLevel level, CompoundTag tag);

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
     * @param level    the level
     * @param capacity the capacity
     * @param owner    the owner, or null if no owner
     * @return the storage disk
     */
    IStorageDisk<T> create(ServerLevel level, int capacity, @Nullable UUID owner);
}
