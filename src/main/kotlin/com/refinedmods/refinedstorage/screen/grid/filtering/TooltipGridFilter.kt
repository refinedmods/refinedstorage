package com.refinedmods.refinedstorage.screen.grid.filtering

import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack
import net.minecraft.util.text.Text
import java.util.function.Predicate

class TooltipGridFilter(tooltip: String) : Predicate<IGridStack> {
    private val tooltip: String
    override fun test(stack: IGridStack): Boolean {
        val tooltip: List<Text?>? = stack.tooltip
        for (i in 1 until tooltip!!.size) {
            if (tooltip[i].getString().toLowerCase().contains(this.tooltip.toLowerCase())) {
                return true
            }
        }
        return false
    }

    init {
        this.tooltip = tooltip.toLowerCase()
    }
}