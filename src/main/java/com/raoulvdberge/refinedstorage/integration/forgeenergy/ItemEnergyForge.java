package com.raoulvdberge.refinedstorage.integration.forgeenergy;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.energy.EnergyStorage;

public class ItemEnergyForge extends EnergyStorage {
    private static final String NBT_ENERGY = "Energy";

    private ItemStack stack;

    public ItemEnergyForge(ItemStack stack, int capacity) {
        super(capacity, Integer.MAX_VALUE, Integer.MAX_VALUE);

        this.stack = stack;
        this.energy = stack.hasTag() && stack.getTag().contains(NBT_ENERGY) ? stack.getTag().getInt(NBT_ENERGY) : 0;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = super.receiveEnergy(maxReceive, simulate);

        if (received > 0 && !simulate) {
            if (!stack.hasTag()) {
                stack.put(new CompoundNBT());
            }

            stack.getTag().putInt(NBT_ENERGY, getEnergyStored());
        }

        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int extracted = super.extractEnergy(maxExtract, simulate);

        if (extracted > 0 && !simulate) {
            if (!stack.hasTag()) {
                stack.put(new CompoundNBT());
            }

            stack.getTag().putInt(NBT_ENERGY, getEnergyStored());
        }

        return extracted;
    }
}
