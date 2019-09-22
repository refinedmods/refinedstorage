package com.raoulvdberge.refinedstorage.item.capabilityprovider;

import com.raoulvdberge.refinedstorage.integration.forgeenergy.ItemEnergyStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EnergyCapabilityProvider implements ICapabilityProvider {
    private ItemStack stack;
    private int energyCapacity;
    private LazyOptional<IEnergyStorage> capability = LazyOptional.of(() -> new ItemEnergyStorage(stack, energyCapacity));

    public EnergyCapabilityProvider(ItemStack stack, int energyCapacity) {
        this.stack = stack;
        this.energyCapacity = energyCapacity;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction dire) {
        if (cap == CapabilityEnergy.ENERGY) {
            return capability.cast();
        }

        return LazyOptional.empty();
    }
}