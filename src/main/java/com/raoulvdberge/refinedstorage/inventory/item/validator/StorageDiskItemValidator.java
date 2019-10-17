package com.raoulvdberge.refinedstorage.inventory.item.validator;

import com.raoulvdberge.refinedstorage.api.storage.disk.IStorageDiskProvider;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class StorageDiskItemValidator implements Predicate<ItemStack> {
    @Override
    public boolean test(ItemStack stack) {
        return stack.getItem() instanceof IStorageDiskProvider && ((IStorageDiskProvider) stack.getItem()).isValid(stack);
    }
}
