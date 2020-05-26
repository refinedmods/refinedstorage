package com.refinedmods.refinedstorage.inventory.item.validator;

import com.refinedmods.refinedstorage.item.UpgradeItem;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class UpgradeItemValidator implements Predicate<ItemStack> {
    private UpgradeItem.Type type;

    public UpgradeItemValidator(UpgradeItem.Type type) {
        this.type = type;
    }

    @Override
    public boolean test(ItemStack stack) {
        return stack.getItem() instanceof UpgradeItem && ((UpgradeItem) stack.getItem()).getType() == type;
    }
}
