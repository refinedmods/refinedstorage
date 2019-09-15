package com.raoulvdberge.refinedstorage.inventory.item.validator;

import com.raoulvdberge.refinedstorage.item.ItemUpgrade;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class ItemValidatorUpgrade implements Predicate<ItemStack> {
    private ItemUpgrade.Type type;

    public ItemValidatorUpgrade(ItemUpgrade.Type type) {
        this.type = type;
    }

    @Override
    public boolean test(ItemStack stack) {
        return stack.getItem() instanceof ItemUpgrade && ((ItemUpgrade) stack.getItem()).getType() == type;
    }
}
