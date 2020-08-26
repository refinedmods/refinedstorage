package com.refinedmods.refinedstorage.inventory.item.validator

import com.refinedmods.refinedstorage.item.UpgradeItem
import net.minecraft.item.ItemStack
import java.util.function.Predicate

class UpgradeItemValidator(private val type: UpgradeItem.Type) : Predicate<ItemStack> {
    override fun test(stack: ItemStack): Boolean {
        return stack.item is UpgradeItem && (stack.item as UpgradeItem).type == type
    }
}