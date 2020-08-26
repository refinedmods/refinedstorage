package com.refinedmods.refinedstorage.screen.grid.filtering

import com.refinedmods.refinedstorage.api.network.grid.IGrid
import com.refinedmods.refinedstorage.api.util.IFilter
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack
import java.util.*
import java.util.function.Predicate

object GridFilterParser {
    fun getFilters(@Nullable grid: IGrid?, query: String?, filters: List<IFilter<*>?>?): List<Predicate<IGridStack?>> {
        val gridFilters: MutableList<Predicate<IGridStack?>>
        val orParts = query!!.split("\\|").toTypedArray()
        if (orParts.size == 1) {
            gridFilters = getFilters(query)
        } else {
            val orPartFilters: MutableList<List<Predicate<IGridStack?>>> = LinkedList()
            for (orPart in orParts) {
                orPartFilters.add(getFilters(orPart))
            }
            gridFilters = LinkedList()
            gridFilters.add(OrGridFilter(orPartFilters))
        }
        if (grid != null) {
            if (grid.viewType == IGrid.VIEW_TYPE_NON_CRAFTABLES) {
                gridFilters.add(CraftableGridFilter(false))
            } else if (grid.viewType == IGrid.VIEW_TYPE_CRAFTABLES) {
                gridFilters.add(CraftableGridFilter(true))
            }
        }
        if (!filters!!.isEmpty()) {
            gridFilters.add(FilterGridFilter(filters))
        }
        return gridFilters
    }

    private fun getFilters(query: String?): MutableList<Predicate<IGridStack?>> {
        val gridFilters: MutableList<Predicate<IGridStack?>> = LinkedList()
        for (part in query!!.toLowerCase().trim { it <= ' ' }.split(" ").toTypedArray()) {
            if (part.startsWith("@")) {
                gridFilters.add(ModGridFilter(part.substring(1)))
            } else if (part.startsWith("#")) {
                gridFilters.add(TooltipGridFilter(part.substring(1)))
            } else if (part.startsWith("$")) {
                gridFilters.add(TagGridFilter(part.substring(1)))
            } else {
                gridFilters.add(NameGridFilter(part))
            }
        }
        return gridFilters
    }
}