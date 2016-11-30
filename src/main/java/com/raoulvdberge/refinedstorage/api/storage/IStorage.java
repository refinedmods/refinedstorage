package com.raoulvdberge.refinedstorage.api.storage;

import net.minecraft.util.NonNullList;

import javax.annotation.Nullable;

public interface IStorage<T> {
    /**
     * @return stacks stored in this storage
     */
    NonNullList<T> getStacks();

    /**
     * @return the amount of fluids stored in this storage
     */
    int getStored();

    /**
     * @return the priority of this storage
     */
    int getPriority();

    /**
     * @return the access type of this storage
     */
    default AccessType getAccessType() {
        return AccessType.INSERT_EXTRACT;
    }

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
