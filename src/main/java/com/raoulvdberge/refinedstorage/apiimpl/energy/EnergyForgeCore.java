package com.raoulvdberge.refinedstorage.apiimpl.energy;

import java.util.Map;
import java.util.UUID;

import com.raoulvdberge.refinedstorage.api.energy.IEnergyCore;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public final class EnergyForgeCore implements IEnergyCore {

	private static final UUID UUIDEmpty = new UUID(0l, 0l);
	protected int capacity;
	protected int energy;

	private final Map<UUID, Integer> powerCells;

	public EnergyForgeCore(int controllerCapacity) {
		this.powerCells = new Object2ObjectOpenHashMap<UUID, Integer>();
		this.powerCells.put(UUIDEmpty, controllerCapacity);
		calculateCapacity();
	}

	private void calculateCapacity() {
		long newCapacity = powerCells.values().stream().mapToLong(Long::valueOf).sum();
		this.capacity = (int)Math.min(newCapacity, Integer.MAX_VALUE);
	}

	@Override
	public void decreaseEnergyCapacity(UUID id, int amount) {
		if (id.equals(UUIDEmpty)) {
			return;
		}
		this.powerCells.remove(id);
		calculateCapacity();
	}

	@Override
	public int extract(int maxExtract, boolean simulate) {
		if (maxExtract <= 0)
			return 0;

		int energyExtracted = Math.min(energy, maxExtract);
		if (!simulate)
			energy -= energyExtracted;
		return energyExtracted;
	}

	@Override
	public int getMaxEnergy() {
		return this.capacity;
	}

	@Override
	public int getStoredEnergy() {
		return this.energy;
	}

	@Override
	public void increaseEnergyCapacity(UUID id, int amount) {
		if (id.equals(UUIDEmpty) || amount <= 0) {
			return;
		}
		this.powerCells.merge(id, amount, (k, v) -> amount);
		calculateCapacity();
	}

	@Override
	public int receive(int maxReceive, boolean simulate) {
		if (maxReceive <= 0)
			return 0;

		int energyReceived = Math.min(capacity - energy, maxReceive);
		if (!simulate)
			energy += energyReceived;
		return energyReceived;
	}

	@Override
	public void setEnergyStored(int energyAmount) {
		this.energy = Math.min(energyAmount, this.capacity);
	}
}
