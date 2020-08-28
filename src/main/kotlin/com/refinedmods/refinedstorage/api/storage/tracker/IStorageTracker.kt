package com.refinedmods.refinedstorage.api.storage.tracker

import net.minecraft.entity.player.PlayerEntity


/**
 * Keeps track of when a stack is changed in the system.
 */
interface IStorageTracker<T> {
    /**
     * Updates the storage tracker entry for a stack, changing it's player name and modification time.
     *
     * @param player player
     * @param stack  the stack
     */
    fun changed(player: PlayerEntity, stack: T)

    /**
     * @param stack the stack
     * @return the entry, or null if the stack hasn't been modified yet
     */
    operator fun get(stack: T): StorageTrackerEntry?
}