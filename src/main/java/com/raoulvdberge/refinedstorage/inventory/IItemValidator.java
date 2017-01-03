package com.raoulvdberge.refinedstorage.inventory;

import com.raoulvdberge.refinedstorage.api.storage.IStorageDiskProvider;
import net.minecraft.item.ItemStack;

public interface IItemValidator {
    IItemValidator STORAGE_DISK = s -> s.getItem() instanceof IStorageDiskProvider && ((IStorageDiskProvider) s.getItem()).create(s).isValid(s);

    boolean isValid(ItemStack stack);
}
