package com.raoulvdberge.refinedstorage.integration.tesla;

import com.raoulvdberge.refinedstorage.RSItems;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.minecraft.item.ItemStack;

public class WirelessGridEnergyTesla implements ITeslaHolder, ITeslaConsumer {
    private ItemStack stack;

    public WirelessGridEnergyTesla(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public long getStoredPower() {
        return RSItems.WIRELESS_GRID.getEnergyStored(stack);
    }

    @Override
    public long getCapacity() {
        return RSItems.WIRELESS_GRID.getMaxEnergyStored(stack);
    }

    @Override
    public long givePower(long power, boolean simulated) {
        return RSItems.WIRELESS_GRID.receiveEnergy(stack, (int) power, simulated);
    }
}
