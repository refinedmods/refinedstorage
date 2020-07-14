package com.refinedmods.refinedstorage.tile;

import com.refinedmods.refinedstorage.apiimpl.API;
import net.minecraft.item.ItemStack;

public class ClientNode {
    private final ItemStack stack;
    private int amount;
    private final int energyUsage;

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

        return energyUsage == ((ClientNode) other).energyUsage && API.instance().getComparer().isEqual(stack, ((ClientNode) other).stack);
    }

    @Override
    public int hashCode() {
        int result = stack.hashCode();
        result = 31 * result + energyUsage;
        return result;
    }
}
