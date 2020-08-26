package com.refinedmods.refinedstorage.api.util

import java.util.*

/**
 * A stack list.
 */
interface IStackList<T> {
    /**
     * Adds a stack to the list, will merge it with another stack if it already exists in the list.
     *
     * @param stack the stack
     * @param size  the size to add
     * @return the result
     */
    fun add(stack: T, size: Int): StackListResult<T>

    /**
     * Adds a stack to the list, will merge it with another stack if it already exists in the list.
     *
     * @param stack the stack
     * @return the result
     */
    fun add(stack: T): StackListResult<T>

    /**
     * Decrements the count of that stack in the list.
     *
     * @param stack the stack
     * @param size  the size to remove
     * @return the result, or null if the stack wasn't present
     */
    
    fun remove(stack: T, size: Int): StackListResult<T>?

    /**
     * Decrements the count of that stack in the list.
     *
     * @param stack the stack
     * @return the result, or null if the stack wasn't present
     */
    fun remove(stack: T): StackListResult<T>?

    /**
     * Returns a stack.
     *
     * @param stack the stack to search for
     * @return the stack, or null if no stack was found
     */
    operator fun get(stack: T): T? {
        return get(stack, IComparer.COMPARE_NBT)
    }

    /**
     * Returns the amount in this list, based on the stack and the flags.
     *
     * @param stack the stack
     * @param flags the flags
     * @return the count, 0 if not found
     */
    fun getCount(stack: T, flags: Int): Int

    /**
     * @param stack the stack
     * @return the count, 0 if not found
     */
    fun getCount(stack: T): Int {
        return getCount(stack, IComparer.COMPARE_NBT)
    }

    /**
     * Returns a stack.
     *
     * @param stack the stack to search for
     * @param flags the flags to compare on, see [IComparer]
     * @return the stack, or null if no stack was found
     */
    operator fun get(stack: T, flags: Int): T?

    /**
     * Returns a stack entry.
     *
     * @param stack the stack to search for
     * @param flags the flags to compare on, see [IComparer]
     * @return the stack entry, or null if no stack entry was found
     */
    fun getEntry(stack: T, flags: Int): StackListEntry<T>?

    /**
     * Returns a stack.
     *
     * @param id the id of the entry to search for
     * @return the stack, or null if no stack was found
     */
    operator fun get(id: UUID): T?

    /**
     * Clears the list.
     */
    fun clear()

    /**
     * @return true if the list is empty, false otherwise
     */
    val isEmpty: Boolean

    /**
     * @return a collection of stacks in this list
     */
    val stacks: Collection<StackListEntry<T>>

    /**
     * @return a new copy of this list, with the stacks in it copied as well
     */
    fun copy(): IStackList<T>
}