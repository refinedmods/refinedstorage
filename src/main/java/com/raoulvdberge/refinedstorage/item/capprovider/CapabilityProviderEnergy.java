package com.raoulvdberge.refinedstorage.item.capprovider;

import com.raoulvdberge.refinedstorage.integration.forgeenergy.ItemEnergyForge;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityProviderEnergy implements ICapabilityProvider {
    private ItemStack stack;
    private int energyCapacity;

    public CapabilityProviderEnergy(ItemStack stack, int energyCapacity) {
        this.stack = stack;
        this.energyCapacity = energyCapacity;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY;
    }

    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY ? CapabilityEnergy.ENERGY.cast(new ItemEnergyForge(stack, energyCapacity)) : null;
    }
}