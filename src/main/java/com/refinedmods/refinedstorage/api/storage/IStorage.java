package com.refinedmods.refinedstorage.api.storage;

import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IComparer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Comparator;

public interface IStorage<T> {
    Comparator<IStorage<?>> COMPARATOR = (left, right) -> {
        int compare = Integer.compare(right.getPriority(), left.getPriority());

        return compare != 0 ? compare : Integer.compare(right.getStored(), left.getStored());
    };

    /**
     * Returns the stacks of the storage.
     * Empty stacks are allowed.
     * Please do not copy the stacks for performance reasons.
     * For the caller: modifying stacks is not allowed!
     *
     * @return stacks stored in this storage, empty stacks are allowed
     */
    Collection<T> getStacks();

    /**
     * Inserts a stack to this storage.
     *
     * @param stack  the stack prototype to insert, can be empty, do NOT modify
     * @param size   the amount of that prototype that has to be inserted
     * @param action the action
     * @return an empty stack if the insert was successful, or a stack with the remainder
     */
    @Nonnull
    T insert(@Nonnull T stack, int size, Action action);

    /**
     * Extracts a stack from this storage.
     * <p>
     * If the stack we found in the system is smaller than the requested size, return that stack anyway.
     *
     * @param stack  a prototype of the stack to extract, can be empty, do NOT modify
     * @param size   the amount of that prototype that has to be extracted
     * @param flags  the flags to compare on, see {@link IComparer}
     * @param action the action
     * @return an empty stack if nothing was extracted, or an extracted stack
     */
    @Nonnull
    T extract(@Nonnull T stack, int size, int flags, Action action);

    /**
     * @return the amount stored in this storage
     */
    int getStored();

    /**
     * @return the priority of this storage
     */
    int getPriority();

    /**
     * @return the access type of this storage
     */
    AccessType getAccessType();

    /**
     * Returns the delta that needs to be added to the item or fluid storage cache AFTER insertion of the stack.
     *
     * @param storedPreInsertion the amount stored pre insertion
     * @param size               the size of the stack being inserted
     * @param remainder          the remainder that we got back, or null if no remainder was there
     * @return the amount to increase the cache with
     */
    int getCacheDelta(int storedPreInsertion, int size, @Nullable T remainder);
}
