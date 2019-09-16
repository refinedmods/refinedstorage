package com.raoulvdberge.refinedstorage.inventory.item.validator;

import com.raoulvdberge.refinedstorage.item.UpgradeItem;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class ItemValidatorUpgrade implements Predicate<ItemStack> {
    private UpgradeItem.Type type;

    public ItemValidatorUpgrade(UpgradeItem.Type type) {
        this.type = type;
    }

    @Override
    public boolean test(ItemStack stack) {
        return stack.getItem() instanceof UpgradeItem && ((UpgradeItem) stack.getItem()).getType() == type;
    }
}
