package com.raoulvdberge.refinedstorage.api.storage;

import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Listens for storage cache changes.
 *
 * @param <T> the type
 */
public interface IStorageCacheListener<T> {
    /**
     * Called when this storage cache listener is attached to a storage cache.
     */
    void onAttached();

    /**
     * Called when the cache invalidates.
     */
    void onInvalidated();

    /**
     * Called when the storage cache changes.
     *
     * @param stack the stack
     * @param size  the size, negative if the amount decreases
     */
    void onChanged(@Nonnull T stack, int size);

    default void onChangedBulk(@Nonnull List<Pair<T, Integer>> stacks)
    {
        for(Pair<T, Integer> stack : stacks)
            onChanged(stack.getLeft(), stack.getRight());
    }
}
