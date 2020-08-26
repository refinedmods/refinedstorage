package com.refinedmods.refinedstorage.screen.grid.sorting

import com.refinedmods.refinedstorage.api.network.grid.IGrid
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.registry.Registry
import net.minecraftforge.fluids.FluidInstance

class IdGridSorter : IGridSorter {
    override fun isApplicable(grid: IGrid?): Boolean {
        return grid!!.sortingType == IGrid.SORTING_TYPE_ID
    }

    override fun compare(left: IGridStack, right: IGridStack, sortingDirection: SortingDirection): Int {
        var leftId = 0
        var rightId = 0
        if (left.ingredient is ItemStack && right.ingredient is ItemStack) {
            leftId = Item.getIdFromItem((left.ingredient as ItemStack).item)
            rightId = Item.getIdFromItem((right.ingredient as ItemStack).item)
        } else if (left.ingredient is FluidInstance && right.ingredient is FluidInstance) {
            leftId = Registry.FLUID.getId((left.ingredient as FluidInstance).getFluid())
            rightId = Registry.FLUID.getId((right.ingredient as FluidInstance).getFluid())
        }
        if (leftId != rightId) {
            if (sortingDirection == SortingDirection.DESCENDING) {
                return Integer.compare(leftId, rightId)
            } else if (sortingDirection == SortingDirection.ASCENDING) {
                return Integer.compare(rightId, leftId)
            }
        }
        return 0
    }
}