package com.raoulvdberge.refinedstorage.api.energy;

import java.util.UUID;

import net.minecraftforge.energy.IEnergyStorage;

public interface IEnergyCore {
	void decreaseEnergyCapacity(UUID id, int amount);

	int extract(int amount, boolean simulate);

	int getMaxEnergy();

	int getStoredEnergy();

	void increaseEnergyCapacity(UUID id, int amount);

	int receive(int amount, boolean simulate);

	void setEnergyStored(int energyAmount);
}