package com.refinedmods.refinedstorage.inventory.item.validator

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternProvider
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import java.util.function.Predicate

class PatternItemValidator(private val world: World) : Predicate<ItemStack> {
    override fun test(stack: ItemStack): Boolean {
        return stack.item is ICraftingPatternProvider && (stack.item as ICraftingPatternProvider).create(world, stack, null)!!.isValid()
    }
}