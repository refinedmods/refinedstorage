package com.refinedmods.refinedstorage.screen.grid.filtering

import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack
import java.util.function.Predicate

class NameGridFilter(name: String) : Predicate<IGridStack> {
    private val name: String
    override fun test(stack: IGridStack): Boolean {
        return stack.name.toLowerCase().contains(name)
    }

    init {
        this.name = name.toLowerCase()
    }
}