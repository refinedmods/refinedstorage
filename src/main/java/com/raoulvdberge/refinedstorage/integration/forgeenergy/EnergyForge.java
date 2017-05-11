package com.raoulvdberge.refinedstorage.integration.forgeenergy;

import net.minecraftforge.energy.EnergyStorage;

public class EnergyForge extends EnergyStorage {
    public EnergyForge(int capacity) {
        super(capacity, capacity, 0);
    }

    public int extractEnergyInternal(int maxExtract) {
        int energyExtracted = Math.min(energy, maxExtract);

        energy -= energyExtracted;

        return energyExtracted;
    }

    public void setMaxEnergyStored(int capacity) {
        this.capacity = capacity;
    }

    public void setEnergyStored(int energy) {
        this.energy = energy;
    }
}
