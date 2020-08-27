package com.refinedmods.refinedstorage.api.storage.disk

import com.refinedmods.refinedstorage.api.storage.IStorage
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.Identifier

/**
 * Represents a storage disk.
 *
 * @param <T> the storage
</T> */
interface IStorageDisk<T> : IStorage<T> {
    /**
     * @return the capacity of this storage disk
     */
    val capacity: Int

    /**
     * When this storage disk is inserted into a storage disk container, it has to adjust to the container's settings
     * and use the following parameters instead.
     *
     * @param listener the listener to be called when the storage changes, or null for no listener
     * @param context  the container context, containing some settings
     */
    fun setSettings(listener: IStorageDiskListener?, context: IStorageDiskContainerContext)

    /**
     * Writes the storage to NBT.
     */
    fun writeToNbt(): CompoundTag?

    /**
     * @return the factory id as registered in [IStorageDiskRegistry]
     */
    val factoryId: Identifier
}