package refinedstorage.tile.controller;

import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageUtils;

public final class ClientSlave {
    public ItemStack stack;
    public int amount;
    public int energyUsage;

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof ClientSlave)) {
            return false;
        }

        return energyUsage == ((ClientSlave) other).energyUsage && RefinedStorageUtils.compareStack(stack, ((ClientSlave) other).stack);
    }

    @Override
    public int hashCode() {
        int result = stack.hashCode();
        result = 31 * result + energyUsage;
        return result;
    }
}
