package com.raoulvdberge.refinedstorage.apiimpl.energy;

import com.raoulvdberge.refinedstorage.api.energy.IEnergy;
import com.raoulvdberge.refinedstorage.api.util.Action;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;
import java.util.UUID;

public final class Energy implements IEnergy {
    private static final UUID DEFAULT_UUID = new UUID(0L, 0L);

    protected int capacity;
    protected int energy;

    private final Map<UUID, Integer> energyStorages;

    public Energy(int controllerCapacity) {
        this.energyStorages = new Object2ObjectOpenHashMap<>();
        this.energyStorages.put(DEFAULT_UUID, controllerCapacity);

        calculateCapacity();
    }

    private void calculateCapacity() {
        long newCapacity = energyStorages.values().stream().mapToLong(Long::valueOf).sum();

        this.capacity = (int) Math.min(newCapacity, Integer.MAX_VALUE);
    }

    @Override
    public void decreaseCapacity(UUID id, int amount) {
        if (id.equals(DEFAULT_UUID)) {
            return;
        }

        this.energyStorages.remove(id);

        calculateCapacity();
    }

    @Override
    public int extract(int maxExtract, Action action) {
        if (maxExtract <= 0) {
            return 0;
        }

        int energyExtracted = Math.min(energy, maxExtract);

        if (action == Action.PERFORM) {
            energy -= energyExtracted;
        }

        return energyExtracted;
    }

    @Override
    public int getCapacity() {
        return this.capacity;
    }

    @Override
    public int getStored() {
        return this.energy;
    }

    @Override
    public void increaseCapacity(UUID id, int amount) {
        if (id.equals(DEFAULT_UUID) || amount <= 0) {
            return;
        }

        this.energyStorages.merge(id, amount, (k, v) -> amount);

        calculateCapacity();
    }

    @Override
    public int insert(int maxReceive, Action action) {
        if (maxReceive <= 0) {
            return 0;
        }

        int energyReceived = Math.min(capacity - energy, maxReceive);

        if (action == Action.PERFORM) {
            energy += energyReceived;
        }

        return energyReceived;
    }

    @Override
    public void setStored(int energy) {
        this.energy = Math.min(energy, this.capacity);
    }
}
