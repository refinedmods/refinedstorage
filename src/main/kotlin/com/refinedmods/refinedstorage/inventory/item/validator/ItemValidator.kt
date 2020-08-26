package com.refinedmods.refinedstorage.inventory.item.validator

import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import java.util.function.Predicate

class ItemValidator(private val item: Item) : Predicate<ItemStack> {
    override fun test(stack: ItemStack): Boolean {
        return stack.item === item
    }
}