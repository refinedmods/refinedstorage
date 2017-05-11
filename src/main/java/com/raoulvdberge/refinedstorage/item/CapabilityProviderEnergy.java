package com.raoulvdberge.refinedstorage.item;

import com.raoulvdberge.refinedstorage.integration.forgeenergy.ItemEnergyForge;
import com.raoulvdberge.refinedstorage.integration.tesla.IntegrationTesla;
import com.raoulvdberge.refinedstorage.integration.tesla.ItemEnergyTesla;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nullable;

public class CapabilityProviderEnergy implements ICapabilityProvider {
    private ItemStack stack;

    public CapabilityProviderEnergy(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityEnergy.ENERGY ||
            (IntegrationTesla.isLoaded() && (capability == TeslaCapabilities.CAPABILITY_HOLDER || capability == TeslaCapabilities.CAPABILITY_CONSUMER));
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(new ItemEnergyForge(stack, 3200));
        }

        if (IntegrationTesla.isLoaded()) {
            if (capability == TeslaCapabilities.CAPABILITY_HOLDER) {
                return TeslaCapabilities.CAPABILITY_HOLDER.cast(new ItemEnergyTesla(stack));
            }

            if (capability == TeslaCapabilities.CAPABILITY_CONSUMER) {
                return TeslaCapabilities.CAPABILITY_CONSUMER.cast(new ItemEnergyTesla(stack));
            }
        }

        return null;
    }
}