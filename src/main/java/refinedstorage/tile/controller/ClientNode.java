package refinedstorage.tile.controller;

import net.minecraft.item.ItemStack;
import refinedstorage.api.storage.CompareUtils;

public final class ClientNode {
    public ItemStack stack;
    public int amount;
    public int energyUsage;

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof ClientNode)) {
            return false;
        }

        return energyUsage == ((ClientNode) other).energyUsage && CompareUtils.compareStack(stack, ((ClientNode) other).stack);
    }

    @Override
    public int hashCode() {
        int result = stack.hashCode();
        result = 31 * result + energyUsage;
        return result;
    }
}
