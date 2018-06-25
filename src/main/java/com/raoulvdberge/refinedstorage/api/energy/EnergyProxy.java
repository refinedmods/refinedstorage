package com.raoulvdberge.refinedstorage.api.energy;

import net.minecraftforge.energy.IEnergyStorage;

public final class EnergyProxy implements IEnergyStorage {
    private final int maxReceive;
    private final int maxExtract;
    private final IEnergy energy;

    public EnergyProxy(IEnergy energy, int maxTransfer) {
        this(energy, maxTransfer, maxTransfer);
    }

    public EnergyProxy(IEnergy energy, int maxReceive, int maxExtract) {
        this.energy = energy;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return !canReceive() ? 0 : this.energy.insert(Math.min(this.maxReceive, maxReceive), simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return !canExtract() ? 0 : this.energy.extract(Math.min(this.maxExtract, maxExtract), simulate);
    }

    @Override
    public int getEnergyStored() {
        return this.energy.getStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return this.energy.getCapacity();
    }

    @Override
    public boolean canExtract() {
        return this.maxExtract > 0;
    }

    @Override
    public boolean canReceive() {
        return this.maxReceive > 0;
    }
}
