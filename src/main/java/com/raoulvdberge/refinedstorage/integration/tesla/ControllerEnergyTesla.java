package com.raoulvdberge.refinedstorage.integration.tesla;

import cofh.api.energy.EnergyStorage;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;

public class ControllerEnergyTesla implements ITeslaHolder, ITeslaConsumer {
    private EnergyStorage energy;

    public ControllerEnergyTesla(EnergyStorage energy) {
        this.energy = energy;
    }

    @Override
    public long givePower(long power, boolean simulated) {
        return energy.receiveEnergy((int) power, simulated);
    }

    @Override
    public long getStoredPower() {
        return energy.getEnergyStored();
    }

    @Override
    public long getCapacity() {
        return energy.getMaxEnergyStored();
    }
}
