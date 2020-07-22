package com.refinedmods.refinedstorage.api.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

/**
 * A stack list.
 */
public interface IStackList<T> {
    /**
     * Adds a stack to the list, will merge it with another stack if it already exists in the list.
     *
     * @param stack the stack
     * @param size  the size to add
     * @return the result
     */
    StackListResult<T> add(@Nonnull T stack, int size);

    /**
     * Adds a stack to the list, will merge it with another stack if it already exists in the list.
     *
     * @param stack the stack
     * @return the result
     */
    StackListResult<T> add(@Nonnull T stack);

    /**
     * Decrements the count of that stack in the list.
     *
     * @param stack the stack
     * @param size  the size to remove
     * @return the result, or null if the stack wasn't present
     */
    @Nullable
    StackListResult<T> remove(@Nonnull T stack, int size);

    /**
     * Decrements the count of that stack in the list.
     *
     * @param stack the stack
     * @return the result, or null if the stack wasn't present
     */
    @Nullable
    StackListResult<T> remove(@Nonnull T stack);

    /**
     * Returns a stack.
     *
     * @param stack the stack to search for
     * @return the stack, or null if no stack was found
     */
    @Nullable
    default T get(@Nonnull T stack) {
        return get(stack, IComparer.COMPARE_NBT);
    }

    /**
     * Returns the amount in this list, based on the stack and the flags.
     *
     * @param stack the stack
     * @param flags the flags
     * @return the count, 0 if not found
     */
    int getCount(@Nonnull T stack, int flags);

    /**
     * @param stack the stack
     * @return the count, 0 if not found
     */
    default int getCount(@Nonnull T stack) {
        return getCount(stack, IComparer.COMPARE_NBT);
    }

    /**
     * Returns a stack.
     *
     * @param stack the stack to search for
     * @param flags the flags to compare on, see {@link IComparer}
     * @return the stack, or null if no stack was found
     */
    @Nullable
    T get(@Nonnull T stack, int flags);

    /**
     * Returns a stack entry.
     *
     * @param stack the stack to search for
     * @param flags the flags to compare on, see {@link IComparer}
     * @return the stack entry, or null if no stack entry was found
     */
    @Nullable
    StackListEntry<T> getEntry(@Nonnull T stack, int flags);

    /**
     * Returns a stack.
     *
     * @param id the id of the entry to search for
     * @return the stack, or null if no stack was found
     */
    @Nullable
    T get(UUID id);

    /**
     * Clears the list.
     */
    void clear();

    /**
     * @return true if the list is empty, false otherwise
     */
    boolean isEmpty();

    /**
     * @return a collection of stacks in this list
     */
    @Nonnull
    Collection<StackListEntry<T>> getStacks();

    /**
     * @return a new copy of this list, with the stacks in it copied as well
     */
    @Nonnull
    IStackList<T> copy();
}
