package refinedstorage.tile.controller;

import net.minecraft.item.ItemStack;
import refinedstorage.RefinedStorageUtils;

public class ClientSideMachine {
    public ItemStack stack;
    public int amount;
    public int energyUsage;

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof ClientSideMachine)) {
            return false;
        }

        return energyUsage == ((ClientSideMachine) other).energyUsage && RefinedStorageUtils.compareStack(stack, ((ClientSideMachine) other).stack);
    }

    @Override
    public int hashCode() {
        int result = stack.hashCode();
        result = 31 * result + energyUsage;
        return result;
    }
}
