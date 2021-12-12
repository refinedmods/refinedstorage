package com.refinedmods.refinedstorage.inventory.item.validator;

import com.refinedmods.refinedstorage.item.UpgradeItem;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class UpgradeItemValidator implements Predicate<ItemStack> {
    private final UpgradeItem.Type type;

    public UpgradeItemValidator(UpgradeItem.Type type) {
        this.type = type;
    }

    @Override
    public boolean test(ItemStack stack) {
        return stack.getItem() instanceof UpgradeItem && ((UpgradeItem) stack.getItem()).getType() == type;
    }
}
