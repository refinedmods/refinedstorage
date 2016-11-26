package com.raoulvdberge.refinedstorage.integration.tesla;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;

public class NetworkItemEnergyTesla implements ITeslaHolder, ITeslaConsumer {
    private ItemStack stack;

    public NetworkItemEnergyTesla(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public long getStoredPower() {
        return stack.getCapability(CapabilityEnergy.ENERGY, null).getEnergyStored();
    }

    @Override
    public long getCapacity() {
        return stack.getCapability(CapabilityEnergy.ENERGY, null).getMaxEnergyStored();
    }

    @Override
    public long givePower(long power, boolean simulated) {
        return stack.getCapability(CapabilityEnergy.ENERGY, null).receiveEnergy((int) power, simulated);
    }
}
