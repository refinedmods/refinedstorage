package com.refinedmods.refinedstorage.api.storage.disk

import net.minecraft.item.ItemStack
import java.util.*


/**
 * Stores storage disks.
 */
interface IStorageDiskManager {
    /**
     * Gets a storage disk by id.
     *
     * @param id the id
     * @return the storage disk, or null if no storage disk is found
     */
    operator fun get(id: UUID): IStorageDisk<*>?

    /**
     * Gets a storage disk by disk stack (a [IStorageDiskProvider]).
     *
     * @param disk the disk stack
     * @return the storage disk, or null if no storage disk is found
     */
    fun getByStack(disk: ItemStack): IStorageDisk<*>?

    /**
     * @return a map of all storage disks
     */
    fun getAll(): Map<UUID, IStorageDisk<*>>

    /**
     * Sets a storage disk.
     *
     * @param id   the id
     * @param disk the disk
     */
    operator fun set(id: UUID, disk: IStorageDisk<*>)

    /**
     * Removes a storage disk.
     *
     * @param id the id
     */
    fun remove(id: UUID)

    /**
     * Marks this manager for saving.
     */
    fun markForSaving()
}