package com.raoulvdberge.refinedstorage.energy;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;

public class ItemEnergyStorage extends EnergyStorage implements INBTSerializable<CompoundNBT> {
    private static final String NBT_ENERGY = "Energy";

    public ItemEnergyStorage(ItemStack stack, int capacity) {
        super(capacity, Integer.MAX_VALUE, Integer.MAX_VALUE);

        this.energy = stack.hasTag() && stack.getTag().contains(NBT_ENERGY) ? stack.getTag().getInt(NBT_ENERGY) : 0;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();

        tag.putInt(NBT_ENERGY, getEnergyStored());

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT tag) {
        this.energy = tag.getInt(NBT_ENERGY);
    }
}
