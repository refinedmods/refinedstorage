package com.refinedmods.refinedstorage.api.storage.disk;

import com.refinedmods.refinedstorage.api.storage.IStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Represents a storage disk.
 *
 * @param <T> the storage
 */
public interface IStorageDisk<T> extends IStorage<T> {
    /**
     * @return the capacity of this storage disk
     */
    int getCapacity();

    /**
     * @return the id of the owner, or null if not present
     */
    @Nullable
    UUID getOwner();

    /**
     * When this storage disk is inserted into a storage disk container, it has to adjust to the container's settings
     * and use the following parameters instead.
     *
     * @param listener the listener to be called when the storage changes, or null for no listener
     * @param context  the container context, containing some settings
     */
    void setSettings(@Nullable IStorageDiskListener listener, IStorageDiskContainerContext context);

    /**
     * Writes the storage to NBT.
     */
    CompoundTag writeToNbt();

    /**
     * @return the factory id as registered in {@link IStorageDiskRegistry}
     */
    ResourceLocation getFactoryId();
}
