package com.raoulvdberge.refinedstorage.api.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * A stack list.
 */
public interface IStackList<T> {
    /**
     * Adds a stack to the list, will merge it with another stack if it already exists in the list.
     *
     * @param stack the stack
     * @param size  the size to add
     */
    void add(@Nonnull T stack, int size);

    /**
     * Adds a stack to the list, will merge it with another stack if it already exists in the list.
     *
     * @param stack the stack
     */
    void add(@Nonnull T stack);

    /**
     * Decrements the count of that stack in the list.
     *
     * @param stack the stack
     * @param size  the size to remove
     * @return true if the remove was successful for the full amount, false otherwise
     */
    boolean remove(@Nonnull T stack, int size);

    /**
     * Decrements the count of that stack in the list.
     *
     * @param stack the stack
     * @return true if the remove was successful for the full amount, false otherwise
     */
    boolean remove(@Nonnull T stack);

    /**
     * Returns a stack.
     *
     * @param stack the stack to search for
     * @return the stack, or null if no stack was found
     */
    @Nullable
    default T get(@Nonnull T stack) {
        return get(stack, IComparer.COMPARE_DAMAGE | IComparer.COMPARE_NBT);
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
     * Returns a stack.
     *
     * @param hash the hash of the stack to search for
     * @return the stack, or null if no stack was found
     */
    @Nullable
    T get(int hash);

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
    Collection<T> getStacks();

    /**
     * @return a new copy of this list, with the stacks in it copied as well
     */
    @Nonnull
    IStackList<T> copy();
}
