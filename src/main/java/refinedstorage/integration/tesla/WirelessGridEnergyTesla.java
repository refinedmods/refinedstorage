package refinedstorage.integration.tesla;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.minecraft.item.ItemStack;
import refinedstorage.item.ItemWirelessGrid;

public class WirelessGridEnergyTesla implements ITeslaHolder, ITeslaConsumer {
    private ItemWirelessGrid wirelessGrid;
    private ItemStack stack;

    public WirelessGridEnergyTesla(ItemWirelessGrid wirelessGrid, ItemStack stack) {
        this.wirelessGrid = wirelessGrid;
        this.stack = stack;
    }

    @Override
    public long getStoredPower() {
        return wirelessGrid.getEnergyStored(stack);
    }

    @Override
    public long getCapacity() {
        return wirelessGrid.getMaxEnergyStored(stack);
    }

    @Override
    public long givePower(long power, boolean simulated) {
        return wirelessGrid.receiveEnergy(stack, (int) power, simulated);
    }
}
