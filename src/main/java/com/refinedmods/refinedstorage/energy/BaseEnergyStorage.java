package com.refinedmods.refinedstorage.energy;

import net.neoforged.neoforge.energy.EnergyStorage;

public class BaseEnergyStorage extends EnergyStorage {
    public BaseEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    // @Volatile: Impl from EnergyStorage#extractEnergy, without the canExtract check
    public void extractEnergyBypassCanExtract(int maxExtract, boolean simulate) {
        int energyExtracted = Math.min(energy, maxExtract);

        if (!simulate) {
            energy -= energyExtracted;
        }
    }

    public void setStored(int energy) {
        this.energy = energy;
    }
}
