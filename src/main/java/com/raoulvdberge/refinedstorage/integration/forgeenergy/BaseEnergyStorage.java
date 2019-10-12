package com.raoulvdberge.refinedstorage.integration.forgeenergy;

import net.minecraftforge.energy.EnergyStorage;

public class BaseEnergyStorage extends EnergyStorage {
    public BaseEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public void setStored(int energy) {
        this.energy = energy;
    }
}
