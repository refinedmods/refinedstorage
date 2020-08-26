package com.refinedmods.refinedstorage.screen.grid.view

import com.refinedmods.refinedstorage.api.network.grid.IGrid
import com.refinedmods.refinedstorage.screen.grid.GridScreen
import com.refinedmods.refinedstorage.screen.grid.filtering.GridFilterParser
import com.refinedmods.refinedstorage.screen.grid.sorting.IGridSorter
import com.refinedmods.refinedstorage.screen.grid.sorting.SortingDirection
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack
import java.util.*

abstract class BaseGridView(private val screen: GridScreen, private val defaultSorter: IGridSorter, private val sorters: List<IGridSorter>) : IGridView {
    private var canCraft = false
    private var stacks: List<IGridStack?> = ArrayList()
    protected val map: Map<UUID, IGridStack?> = HashMap()
    override fun getStacks(): List<IGridStack?> {
        return stacks
    }

    @Nullable
    override fun get(id: UUID?): IGridStack? {
        return map[id]
    }

    override fun sort() {
        if (!screen.canSort()) {
            return
        }
        val stacks: MutableList<IGridStack?> = ArrayList()
        if (screen.grid.isGridActive) {
            stacks.addAll(map.values)
            val grid = screen.grid
            val filters = GridFilterParser.getFilters(
                    grid,
                    screen.searchFieldText,
                    if (grid!!.tabSelected >= 0 && grid.tabSelected < grid.tabs!!.size) grid.tabs!![grid.tabSelected]!!.filters else grid.filters
            )
            stacks.removeIf { stack: IGridStack? ->
                // If this is a crafting stack,
                // and there is a regular matching stack in the view too,
                // and we aren't in "view only craftables" mode,
                // we don't want the duplicate stacks and we will remove this stack.
                if (screen.grid.viewType != IGrid.VIEW_TYPE_CRAFTABLES &&
                        stack!!.isCraftable && stack.otherId != null &&
                        map.containsKey(stack.otherId)) {
                    return@removeIf true
                }
                for (filter in filters!!) {
                    if (!filter!!.test(stack)) {
                        return@removeIf true
                    }
                }
                false
            }
            val sortingDirection = if (grid.sortingDirection == IGrid.SORTING_DIRECTION_DESCENDING) SortingDirection.DESCENDING else SortingDirection.ASCENDING
            stacks.sort(java.util.Comparator<IGridStack> { left: IGridStack, right: IGridStack -> defaultSorter.compare(left, right, sortingDirection) })
            for (sorter in sorters) {
                if (sorter.isApplicable(grid)) {
                    stacks.sort(java.util.Comparator<IGridStack> { left: IGridStack, right: IGridStack -> sorter.compare(left, right, sortingDirection) })
                }
            }
        }
        this.stacks = stacks
        screen.updateScrollbar()
    }

    override fun setCanCraft(canCraft: Boolean) {
        this.canCraft = canCraft
    }

    override fun canCraft(): Boolean {
        return canCraft
    }
}