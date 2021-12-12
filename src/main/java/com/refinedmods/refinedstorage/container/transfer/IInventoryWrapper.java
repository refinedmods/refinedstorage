package com.refinedmods.refinedstorage.container.transfer;

import net.minecraft.world.item.ItemStack;

interface IInventoryWrapper {
    InsertionResult insert(ItemStack stack);
}
