package com.raoulvdberge.refinedstorage.inventory;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class ItemValidatorBasic implements Predicate<ItemStack> {
    private Item item;
    private int damage = -1;

    public ItemValidatorBasic(Item item) {
        this.item = item;
    }

    public ItemValidatorBasic(Item item, int damage) {
        this.item = item;
        this.damage = damage;
    }

    @Override
    public boolean test(ItemStack stack) {
        return stack.getItem() == item && !(damage != -1 && stack.getItemDamage() != damage);
    }
}
