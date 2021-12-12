package com.refinedmods.refinedstorage.inventory.item.validator;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class ItemValidator implements Predicate<ItemStack> {
    private final Item item;

    public ItemValidator(Item item) {
        this.item = item;
    }

    @Override
    public boolean test(ItemStack stack) {
        return stack.getItem() == item;
    }
}
