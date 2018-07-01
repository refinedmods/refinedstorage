package com.raoulvdberge.refinedstorage.api.energy;

import com.raoulvdberge.refinedstorage.api.util.Action;

import java.util.UUID;

public interface IEnergy {
    void decreaseCapacity(UUID id, int amount);

    int extract(int amount, Action action);

    int getCapacity();

    int getStored();

    void increaseCapacity(UUID id, int amount);

    int insert(int amount, Action action);

    void setStored(int energyAmount);
}
