package com.refinedmods.refinedstorage.screen.grid.filtering

import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack
import java.util.function.Predicate

class CraftableGridFilter(private val craftable: Boolean) : Predicate<IGridStack> {
    override fun test(stack: IGridStack): Boolean {
        return if (craftable) {
            stack.isCraftable
        } else {
            !stack.isCraftable && stack.otherId == null
        }
    }
}