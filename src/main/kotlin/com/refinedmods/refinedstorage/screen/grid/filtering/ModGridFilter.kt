package com.refinedmods.refinedstorage.screen.grid.filtering

import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack
import java.util.function.Predicate

class ModGridFilter(inputModName: String) : Predicate<IGridStack> {
    private val inputModName: String
    override fun test(stack: IGridStack): Boolean {
        val modId = stack.modId
        if (modId != null) {
            if (modId.contains(inputModName)) {
                return true
            }
            var modName = stack.modName
            if (modName != null) {
                modName = standardify(modName)
                if (modName.contains(inputModName)) {
                    return true
                }
            }
        }
        return false
    }

    private fun standardify(input: String): String {
        return input.toLowerCase().replace(" ", "")
    }

    init {
        this.inputModName = standardify(inputModName)
    }
}