package com.raoulvdberge.refinedstorage.api.storage.disk;

import com.raoulvdberge.refinedstorage.api.storage.IStorage;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

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
    NBTTagCompound writeToNbt();

    /**
     * @return the factory id as registered in {@link IStorageDiskRegistry}
     */
    String getId();
}
