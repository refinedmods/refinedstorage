package com.refinedmods.refinedstorage.util;

import com.refinedmods.refinedstorage.apiimpl.API;
import net.minecraft.world.item.ItemStack;

public final class ItemStackKey {
    private final ItemStack stack;

    public ItemStackKey(ItemStack stack) {
        this.stack = stack;
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ItemStackKey && API.instance().getComparer().isEqualNoQuantity(stack, ((ItemStackKey) other).stack);
    }

    @Override
    public int hashCode() {
        return API.instance().getItemStackHashCode(stack);
    }
}
