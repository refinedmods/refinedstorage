package com.raoulvdberge.refinedstorage.integration.tesla;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.minecraftforge.energy.IEnergyStorage;

public class ControllerEnergyTesla implements ITeslaHolder, ITeslaConsumer {
    private IEnergyStorage energy;

    public ControllerEnergyTesla(IEnergyStorage energy) {
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
