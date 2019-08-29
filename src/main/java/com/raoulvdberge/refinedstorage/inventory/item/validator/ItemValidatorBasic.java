package com.raoulvdberge.refinedstorage.inventory.item.validator;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class ItemValidatorBasic implements Predicate<ItemStack> {
    private Item item;

    public ItemValidatorBasic(Item item) {
        this.item = item;
    }

    @Override
    public boolean test(ItemStack stack) {
        return stack.getItem() == item;
    }
}
