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

    private final Map<UUID, Integer> storages;

    public Energy(int controllerCapacity) {
        this.storages = new Object2ObjectOpenHashMap<>();
        this.storages.put(DEFAULT_UUID, controllerCapacity);

        calculateCapacity();
    }

    private void calculateCapacity() {
        long newCapacity = storages.values().stream().mapToLong(Long::valueOf).sum();

        this.capacity = (int) Math.min(newCapacity, Integer.MAX_VALUE);
    }

    @Override
    public void removeCapacity(UUID id, int amount) {
        if (id.equals(DEFAULT_UUID)) {
            return;
        }

        this.storages.remove(id);

        calculateCapacity();
    }

    @Override
    public int extract(int maxExtract, Action action) {
        if (maxExtract <= 0) {
            return 0;
        }

        int extracted = Math.min(energy, maxExtract);

        if (action == Action.PERFORM) {
            energy -= extracted;
        }

        return extracted;
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
    public void addCapacity(UUID id, int amount) {
        if (id.equals(DEFAULT_UUID) || amount <= 0) {
            return;
        }

        this.storages.merge(id, amount, (k, v) -> amount);

        calculateCapacity();
    }

    @Override
    public int insert(int maxReceive, Action action) {
        if (maxReceive <= 0) {
            return 0;
        }

        int inserted = Math.min(capacity - energy, maxReceive);

        if (action == Action.PERFORM) {
            energy += inserted;
        }

        return inserted;
    }

    @Override
    public void setStored(int amount) {
        this.energy = Math.min(amount, this.capacity);
    }
}
