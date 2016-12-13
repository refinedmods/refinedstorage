package com.raoulvdberge.refinedstorage.integration.forgeenergy;

import com.raoulvdberge.refinedstorage.RS;
import net.minecraftforge.energy.EnergyStorage;

public class ControllerEnergyForge extends EnergyStorage {
    public ControllerEnergyForge() {
        super(RS.INSTANCE.config.controllerCapacity, Integer.MAX_VALUE, 0);
    }

    public int extractEnergyInternal(int maxExtract, boolean simulate) {
        int energyExtracted = Math.min(energy, maxExtract);

        if (!simulate) {
            energy -= energyExtracted;
        }

        return energyExtracted;
    }

    public void setMaxEnergyStored(int capacity) {
        this.capacity = capacity;
    }

    public void setEnergyStored(int energy) {
        this.energy = energy;
    }
}
