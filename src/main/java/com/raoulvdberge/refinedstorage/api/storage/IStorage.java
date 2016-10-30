package com.raoulvdberge.refinedstorage.api.storage;

import java.util.List;

public interface IStorage<T> {
    /**
     * @return stacks stored in this storage
     */
    List<T> getStacks();

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
}
