package com.raoulvdberge.refinedstorage.api.util;

import com.raoulvdberge.refinedstorage.api.IRSAPI;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * A fluid stack list.
 */
public interface IFluidStackList {
    /**
     * Adds a stack to the list, will merge it with another stack if it already exists in the list.
     *
     * @param stack the stack
     */
    void add(FluidStack stack);

    /**
     * Decrements the count of that stack in the list.
     *
     * @param stack               the stack
     * @param size                the size to remove
     * @param removeIfReachedZero true to remove the stack if the count reaches 0, false otherwise
     * @return whether the remove was successful for the full amount
     */
    boolean remove(@Nonnull FluidStack stack, int size, boolean removeIfReachedZero);

    /**
     * Decrements the count of that stack in the list.
     *
     * @param stack               the stack
     * @param removeIfReachedZero true to remove the stack if the count reaches 0, false otherwise
     * @return whether the remove was successful for the full amount
     */
    default boolean remove(@Nonnull FluidStack stack, boolean removeIfReachedZero) {
        return remove(stack, stack.amount, removeIfReachedZero);
    }

    /**
     * Decrements the count of that stack in the list.
     * Keeps track of remove items and can be undone by calling {@link #undo()}
     *
     * @param stack               the stack
     * @param size                the size to remove
     * @param removeIfReachedZero true to remove the stack if the count reaches 0, false otherwise
     * @return whether the remove was successful for the full amount
     */
    boolean trackedRemove(@Nonnull FluidStack stack, int size, boolean removeIfReachedZero);

    /**
     * Decrements the count of that stack in the list.
     * Keeps track of remove items and can be undone by calling {@link #undo()}
     *
     * @param stack               the stack
     * @param removeIfReachedZero true to remove the stack if the count reaches 0, false otherwise
     * @return whether the remove was successful for the full amount
     */
    default boolean trackedRemove(@Nonnull FluidStack stack, boolean removeIfReachedZero) {
        return trackedRemove(stack, stack.amount, removeIfReachedZero);
    }

    /**
     * Restore all tracked removes
     */
    void undo();

    /**
     * Returns a stack.
     *
     * @param stack the stack to search for
     * @return the stack, or null if no stack was found
     */
    @Nullable
    default FluidStack get(@Nonnull FluidStack stack) {
        return get(stack, IComparer.COMPARE_NBT);
    }

    /**
     * Returns a stack.
     *
     * @param stack the stack to search for
     * @param flags the flags to compare on, see {@link IComparer}
     * @return the stack, or null if no stack was found
     */
    @Nullable
    FluidStack get(@Nonnull FluidStack stack, int flags);

    /**
     * Returns a stack.
     *
     * @param hash the hash of the stack to search for, see {@link IRSAPI#getFluidStackHashCode(FluidStack)}
     * @return the stack, or null if no stack was found
     */
    @Nullable
    FluidStack get(int hash);

    /**
     * Clears the list.
     */
    void clear();

    /**
     * Removes all stacks with size zero
     */
    void clean();

    /**
     * @return true if the list is empty, false otherwise
     */
    boolean isEmpty();

    /**
     * @return a collection of stacks in this list
     */
    @Nonnull
    Collection<FluidStack> getStacks();

    /**
     * @return a new copy of this list, with the stacks in it copied as well
     */
    @Nonnull
    IFluidStackList copy();
}
