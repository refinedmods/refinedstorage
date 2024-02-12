package com.refinedmods.refinedstorage.inventory.fluid;

import com.refinedmods.refinedstorage.inventory.listener.InventoryListener;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class FluidInventory {
    private static final String NBT_SLOT = "Slot_%d";

    private final List<InventoryListener<FluidInventory>> listeners = new ArrayList<>();

    private final FluidStack[] fluids;
    private final int maxAmount;
    private boolean empty = true;

    public FluidInventory(int size, int maxAmount) {
        this.fluids = new FluidStack[size];

        for (int i = 0; i < size; ++i) {
            fluids[i] = FluidStack.EMPTY;
        }

        this.maxAmount = maxAmount;
    }

    public FluidInventory(int size) {
        this(size, Integer.MAX_VALUE);
    }

    public FluidInventory addListener(InventoryListener<FluidInventory> listener) {
        listeners.add(listener);

        return this;
    }

    public int getSlots() {
        return fluids.length;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public FluidStack[] getFluids() {
        return fluids;
    }

    @Nonnull
    public FluidStack getFluid(int slot) {
        return fluids[slot];
    }

    public void setFluid(int slot, @Nonnull FluidStack stack) {
        if (stack.getAmount() > maxAmount) {
            throw new IllegalArgumentException("Fluid size is invalid (given: " + stack.getAmount() + ", max size: " + maxAmount + ")");
        }

        fluids[slot] = stack;

        onChanged(slot);
    }

    public void onChanged(int slot) {
        listeners.forEach(l -> l.onChanged(this, slot, false));
        updateEmptyState();
    }

    public CompoundTag writeToNbt() {
        CompoundTag tag = new CompoundTag();

        for (int i = 0; i < getSlots(); ++i) {
            FluidStack stack = getFluid(i);

            if (!stack.isEmpty()) {
                tag.put(String.format(NBT_SLOT, i), stack.writeToNBT(new CompoundTag()));
            }
        }

        return tag;
    }

    public void readFromNbt(CompoundTag tag) {
        for (int i = 0; i < getSlots(); ++i) {
            String key = String.format(NBT_SLOT, i);

            if (tag.contains(key)) {
                fluids[i] = FluidStack.loadFluidStackFromNBT(tag.getCompound(key));
            }
        }

        updateEmptyState();
    }

    private void updateEmptyState() {
        this.empty = true;

        for (FluidStack fluid : fluids) {
            if (!fluid.isEmpty()) {
                this.empty = false;

                return;
            }
        }
    }

    public boolean isEmpty() {
        return empty;
    }
}
