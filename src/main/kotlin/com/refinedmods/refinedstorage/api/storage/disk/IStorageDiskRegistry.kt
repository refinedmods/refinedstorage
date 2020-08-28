package com.refinedmods.refinedstorage.api.storage.disk

import net.minecraft.util.Identifier


/**
 * Stores factories for reproducing storage disks from disk.
 */
interface IStorageDiskRegistry {
    /**
     * Adds a factory.
     *
     * @param id      the id of this factory
     * @param factory the factory
     */
    fun add(id: Identifier?, factory: IStorageDiskFactory<*>?)

    /**
     * Gets a factory.
     *
     * @param id the factory id
     * @return the factory, or null if no factory was found
     */
    operator fun get(id: Identifier): IStorageDiskFactory<*>?
}