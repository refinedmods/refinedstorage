package refinedstorage.tile.controller;

import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageUtils;

public class ClientMachine {
    public ItemStack stack;
    public int amount;
    public int energyUsage;

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof ClientMachine)) {
            return false;
        }

        return energyUsage == ((ClientMachine) other).energyUsage && RefinedStorageUtils.compareStack(stack, ((ClientMachine) other).stack);
    }

    @Override
    public int hashCode() {
        int result = stack.hashCode();
        result = 31 * result + energyUsage;
        return result;
    }
}
