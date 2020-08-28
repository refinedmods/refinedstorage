package com.refinedmods.refinedstorage.api.storage

import com.refinedmods.refinedstorage.api.util.Action
import com.refinedmods.refinedstorage.api.util.IComparer

interface IStorage<T> {
    /**
     * Returns the stacks of the storage.
     * Empty stacks are allowed.
     * Please do not copy the stacks for performance reasons.
     * For the caller: modifying stacks is not allowed!
     *
     * @return stacks stored in this storage, empty stacks are allowed
     */
    fun getStacks(): Collection<T>

    /**
     * Inserts a stack to this storage.
     *
     * @param stack  the stack prototype to insert, can be empty, do NOT modify
     * @param size   the amount of that prototype that has to be inserted
     * @param action the action
     * @return an empty stack if the insert was successful, or a stack with the remainder
     */
    fun insert(stack: T, size: Int, action: Action): T

    /**
     * Extracts a stack from this storage.
     *
     *
     * If the stack we found in the system is smaller than the requested size, return that stack anyway.
     *
     * @param stack  a prototype of the stack to extract, can be empty, do NOT modify
     * @param size   the amount of that prototype that has to be extracted
     * @param flags  the flags to compare on, see [IComparer]
     * @param action the action
     * @return an empty stack if nothing was extracted, or an extracted stack
     */
    fun extract(stack: T, size: Int, flags: Int, action: Action): T

    /**
     * @return the amount stored in this storage
     */
    fun getStored(): Int

    /**
     * @return the priority of this storage
     */
    fun getPriority(): Int

    /**
     * @return the access type of this storage
     */
    fun getAccessType(): AccessType

    /**
     * Returns the delta that needs to be added to the item or fluid storage cache AFTER insertion of the stack.
     *
     * @param storedPreInsertion the amount stored pre insertion
     * @param size               the size of the stack being inserted
     * @param remainder          the remainder that we got back, or null if no remainder was there
     * @return the amount to increase the cache with
     */
    fun getCacheDelta(storedPreInsertion: Int, size: Int, remainder: T): Int

    companion object {
        val COMPARATOR = java.util.Comparator { left: IStorage<*>, right: IStorage<*> ->
            val compare = right.getPriority().compareTo(left.getPriority())
            if (compare != 0) compare else right.getStored().compareTo(left.getStored())
        }
    }
}