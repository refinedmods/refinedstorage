package com.raoulvdberge.refinedstorage.api.energy;

import java.util.UUID;

public interface IEnergy {
	void decreaseCapacity(UUID id, int amount);

	int extract(int amount, boolean simulate);

	int getCapacity();

	int getStored();

	void increaseCapacity(UUID id, int amount);

	int insert(int amount, boolean simulate);

	void setStored(int energyAmount);
}