package com.raoulvdberge.refinedstorage.api.energy;

import java.util.UUID;

public interface IEnergy {
	void decreaseCapacity(UUID id, int amount);

	int extract(int amount, boolean simulate);

	int getMaxEnergy();

	int getStored();

	void increaseCapacity(UUID id, int amount);

	int receive(int amount, boolean simulate);

	void setStored(int energyAmount);
}