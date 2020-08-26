package com.refinedmods.refinedstorage.screen.grid.filtering

import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack
import java.util.function.Predicate

class OrGridFilter(private val orPartFilters: List<List<Predicate<IGridStack?>>>) : Predicate<IGridStack?> {
    override fun test(gridStack: IGridStack?): Boolean {
        for (orPart in orPartFilters) {
            for (part in orPart) {
                if (part.test(gridStack)) {
                    return true
                }
            }
        }
        return false
    }
}