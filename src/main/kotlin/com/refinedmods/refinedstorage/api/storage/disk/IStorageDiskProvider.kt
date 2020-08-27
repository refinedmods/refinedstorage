package com.refinedmods.refinedstorage.api.storage.disk

import com.refinedmods.refinedstorage.api.storage.StorageType
import net.minecraft.item.ItemStack
import java.util.*


/**
 * Maps disk items to an id.
 * Implement this on a disk item.
 */
interface IStorageDiskProvider {
    /**
     * @param disk the disk
     * @return the id of the disk
     */
    fun getId(disk: ItemStack?): UUID?

    /**
     * @param disk the disk
     * @param id   the id to set
     */
    fun setId(disk: ItemStack?, id: UUID?)

    /**
     * @param disk the disk
     * @return true if the given disk has an id set, false otherwise
     */
    fun isValid(disk: ItemStack?): Boolean

    /**
     * @param disk the disk
     * @return the capacity of the given disk
     */
    fun getCapacity(disk: ItemStack?): Int

    /**
     * @return the storage type
     */
    fun getType(): StorageType?
}