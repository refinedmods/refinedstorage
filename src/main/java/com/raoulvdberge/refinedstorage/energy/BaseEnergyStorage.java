package com.raoulvdberge.refinedstorage.energy;

import net.minecraftforge.energy.EnergyStorage;

public class BaseEnergyStorage extends EnergyStorage {
    public BaseEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    // @Volatile: Impl from EnergyStorage#extractEnergy, without the canExtract check
    public int extractEnergyBypassCanExtract(int maxExtract, boolean simulate) {
        int energyExtracted = Math.min(energy, maxExtract);

        if (!simulate) {
            energy -= energyExtracted;
        }

        return energyExtracted;
    }

    public void setStored(int energy) {
        this.energy = energy;
    }
}
