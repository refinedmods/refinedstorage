package com.refinedmods.refinedstorage.container.transfer;

import net.minecraft.item.ItemStack;

interface IInventoryWrapper {
    InsertionResult insert(ItemStack stack);
}
