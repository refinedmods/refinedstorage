package refinedstorage.tile;

import net.minecraft.item.ItemStack;
import refinedstorage.api.storage.CompareUtils;

public class ClientNode {
    private ItemStack stack;
    private int amount;
    private int energyUsage;

    public ClientNode(ItemStack stack, int amount, int energyUsage) {
        this.stack = stack;
        this.amount = amount;
        this.energyUsage = energyUsage;
    }

    public ItemStack getStack() {
        return stack;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getEnergyUsage() {
        return energyUsage;
    }

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
