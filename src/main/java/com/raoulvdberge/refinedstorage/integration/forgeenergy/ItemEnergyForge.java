package com.raoulvdberge.refinedstorage.integration.forgeenergy;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.EnergyStorage;

public class ItemEnergyForge extends EnergyStorage {
    private static final String NBT_ENERGY = "Energy";

    private ItemStack stack;

    public ItemEnergyForge(ItemStack stack, int capacity) {
        super(capacity, Integer.MAX_VALUE, Integer.MAX_VALUE);

        this.stack = stack;
        this.energy = stack.hasTagCompound() && stack.getTagCompound().hasKey(NBT_ENERGY) ? stack.getTagCompound().getInteger(NBT_ENERGY) : 0;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = super.receiveEnergy(maxReceive, simulate);

        if (received > 0 && !simulate) {
            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new NBTTagCompound());
            }

            stack.getTagCompound().setInteger(NBT_ENERGY, getEnergyStored());
        }

        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int extracted = super.extractEnergy(maxExtract, simulate);

        if (extracted > 0 && !simulate) {
            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new NBTTagCompound());
            }

            stack.getTagCompound().setInteger(NBT_ENERGY, getEnergyStored());
        }

        return extracted;
    }
}
