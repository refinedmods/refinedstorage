package com.refinedmods.refinedstorage.screen.grid.filtering

import com.refinedmods.refinedstorage.api.util.IFilter
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.screen.grid.stack.FluidGridStack
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack
import com.refinedmods.refinedstorage.screen.grid.stack.ItemGridStack
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraftforge.fluids.FluidInstance
import java.util.function.Predicate

class FilterGridFilter(private val filters: List<IFilter<*>?>?) : Predicate<IGridStack> {
    override fun test(stack: IGridStack): Boolean {
        if (filters!!.isEmpty()) {
            return true
        }
        var lastMode = IFilter.MODE_WHITELIST
        for (filter in filters) {
            lastMode = filter!!.mode
            if (stack is ItemGridStack && filter.stack is ItemStack) {
                val stackInFilter = filter.stack as ItemStack
                if (filter.isModFilter) {
                    val stackModId = stack.getModId()
                    val filterModId: String = stackInFilter.item.getCreatorModId(stackInFilter)
                    if (filterModId != null && filterModId == stackModId) {
                        return filter.mode == IFilter.MODE_WHITELIST
                    }
                } else if (instance().getComparer()!!.isEqual(stack.stack, stackInFilter, filter.compare)) {
                    return filter.mode == IFilter.MODE_WHITELIST
                }
            } else if (stack is FluidGridStack && filter.stack is FluidInstance) {
                val stackInFilter: FluidInstance = filter.stack as FluidInstance
                if (filter.isModFilter) {
                    val stackInFilterRegistryName: Identifier = stackInFilter.getFluid().getRegistryName()
                    if (stackInFilterRegistryName != null) {
                        val stackInFilterModId: String = stackInFilterRegistryName.getNamespace()
                        if (stackInFilterModId.equals(stack.getModId(), ignoreCase = true)) {
                            return filter.mode == IFilter.MODE_WHITELIST
                        }
                    }
                } else if (instance().getComparer().isEqual(stack.stack, stackInFilter, filter.compare)) {
                    return filter.mode == IFilter.MODE_WHITELIST
                }
            }
        }
        return lastMode != IFilter.MODE_WHITELIST
    }
}