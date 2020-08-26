package com.refinedmods.refinedstorage.inventory.item.validator

import com.refinedmods.refinedstorage.api.storage.disk.IStorageDiskProvider
import net.minecraft.item.ItemStack
import java.util.function.Predicate

class StorageDiskItemValidator : Predicate<ItemStack> {
    override fun test(stack: ItemStack): Boolean {
        return stack.item is IStorageDiskProvider && (stack.item as IStorageDiskProvider).isValid(stack)
    }
}