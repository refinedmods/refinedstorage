package com.raoulvdberge.refinedstorage.api.energy;

import net.minecraftforge.energy.IEnergyStorage;

public final class EnergyForgeCoreProxy implements IEnergyStorage {

	private final int maxReceive;
	private final int maxExtract;
	private final IEnergyCore energyCore;

	public EnergyForgeCoreProxy(IEnergyCore energyCore, int maxTransfer) {
		this(energyCore, maxTransfer, maxTransfer);
	}

	public EnergyForgeCoreProxy(IEnergyCore energyCore, int maxReceive, int maxExtract) {
		this.energyCore = energyCore;
		this.maxReceive = maxReceive;
		this.maxExtract = maxExtract;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return !canReceive() ? 0 : this.energyCore.receive(Math.min(this.maxReceive, maxReceive), simulate);
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return !canExtract() ? 0 : this.energyCore.extract(Math.min(this.maxExtract, maxExtract), simulate);
	}

	@Override
	public int getEnergyStored() {
		return this.energyCore.getStoredEnergy();
	}

	@Override
	public int getMaxEnergyStored() {
		return this.energyCore.getMaxEnergy();
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