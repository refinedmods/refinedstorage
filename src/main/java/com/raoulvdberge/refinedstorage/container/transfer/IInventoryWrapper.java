package com.raoulvdberge.refinedstorage.container.transfer;

import net.minecraft.item.ItemStack;

interface IInventoryWrapper {
    InsertionResult insert(ItemStack stack);
}
