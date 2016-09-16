package refinedstorage.integration.tesla;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageItems;

public class WirelessGridEnergyTesla implements ITeslaHolder, ITeslaConsumer {
    private ItemStack stack;

    public WirelessGridEnergyTesla(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public long getStoredPower() {
        return RefinedStorageItems.WIRELESS_GRID.getEnergyStored(stack);
    }

    @Override
    public long getCapacity() {
        return RefinedStorageItems.WIRELESS_GRID.getMaxEnergyStored(stack);
    }

    @Override
    public long givePower(long power, boolean simulated) {
        return RefinedStorageItems.WIRELESS_GRID.receiveEnergy(stack, (int) power, simulated);
    }
}
