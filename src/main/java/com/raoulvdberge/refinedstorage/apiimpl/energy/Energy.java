package com.raoulvdberge.refinedstorage.apiimpl.energy;

import java.util.Map;
import java.util.UUID;

import com.raoulvdberge.refinedstorage.api.energy.IEnergy;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public final class Energy implements IEnergy {

	private static final UUID UUID_EMPTY = new UUID(0l, 0l);

	protected int capacity;
	protected int energy;

	private final Map<UUID, Integer> energyStorages;

	public Energy(int controllerCapacity) {
		this.energyStorages = new Object2ObjectOpenHashMap<UUID, Integer>();
		this.energyStorages.put(UUID_EMPTY, controllerCapacity);
		calculateCapacity();
	}

	private void calculateCapacity() {
		long newCapacity = energyStorages.values().stream().mapToLong(Long::valueOf).sum();
		this.capacity = (int) Math.min(newCapacity, Integer.MAX_VALUE);
	}

	@Override
	public void decreaseCapacity(UUID id, int amount) {
		if (id.equals(UUID_EMPTY)) {
			return;
		}
		this.energyStorages.remove(id);
		calculateCapacity();
	}

	@Override
	public int extract(int maxExtract, boolean simulate) {
		if (maxExtract <= 0) {
			return 0;
		}

		int energyExtracted = Math.min(energy, maxExtract);
		if (!simulate) {
			energy -= energyExtracted;
		}
		return energyExtracted;
	}

	@Override
	public int getMaxEnergy() {
		return this.capacity;
	}

	@Override
	public int getStored() {
		return this.energy;
	}

	@Override
	public void increaseCapacity(UUID id, int amount) {
		if (id.equals(UUID_EMPTY) || amount <= 0) {
			return;
		}
		this.energyStorages.merge(id, amount, (k, v) -> amount);
		calculateCapacity();
	}

	@Override
	public int receive(int maxReceive, boolean simulate) {
		if (maxReceive <= 0) {
			return 0;
		}

		int energyReceived = Math.min(capacity - energy, maxReceive);
		if (!simulate) {
			energy += energyReceived;
		}
		return energyReceived;
	}

	@Override
	public void setStored(int energyAmount) {
		this.energy = Math.min(energyAmount, this.capacity);
	}
}
