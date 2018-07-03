package com.raoulvdberge.refinedstorage.api.energy;

import com.raoulvdberge.refinedstorage.api.util.Action;

import java.util.UUID;

/**
 * An energy container.
 */
public interface IEnergy {
    /**
     * @param id     id of the storage
     * @param amount the amount
     */
    void removeCapacity(UUID id, int amount);

    /**
     * @param id     id of the storage
     * @param amount the amount
     */
    void addCapacity(UUID id, int amount);

    /**
     * @return the capacity
     */
    int getCapacity();

    /**
     * @return the amount stored
     */
    int getStored();

    /**
     * @param amount the amount stored
     */
    void setStored(int amount);

    /**
     * @param amount the amount
     * @param action the action
     * @return the energy extracted
     */
    int extract(int amount, Action action);

    /**
     * @param amount the amount
     * @param action the action
     * @return the energy inserted
     */
    int insert(int amount, Action action);
}
