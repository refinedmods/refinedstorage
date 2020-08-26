package com.refinedmods.refinedstorage.inventory.item

import com.refinedmods.refinedstorage.RSItems
import com.refinedmods.refinedstorage.api.network.grid.IGridTab
import com.refinedmods.refinedstorage.api.util.IFilter
import com.refinedmods.refinedstorage.apiimpl.network.grid.GridTab
import com.refinedmods.refinedstorage.apiimpl.util.FluidFilter
import com.refinedmods.refinedstorage.apiimpl.util.ItemFilter
import com.refinedmods.refinedstorage.inventory.fluid.FilterFluidInventory
import com.refinedmods.refinedstorage.inventory.item.validator.ItemValidator
import com.refinedmods.refinedstorage.item.FilterItem
import com.refinedmods.refinedstorage.screen.BaseScreen
import com.refinedmods.refinedstorage.screen.grid.GridScreen
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidInstance
import net.minecraftforge.fml.LogicalSide
import net.minecraftforge.fml.common.thread.EffectiveSide
import java.util.*

class FilterItemHandler(private val filters: MutableList<IFilter<*>>, private val tabs: MutableList<IGridTab>) : BaseItemHandler(4) {
    override fun onContentsChanged(slot: Int) {
        super.onContentsChanged(slot)
        filters.clear()
        tabs.clear()
        for (i in 0 until getSlots()) {
            val filter: ItemStack = getStackInSlot(i)
            if (!filter.isEmpty) {
                addFilter(filter)
            }
        }
        if (EffectiveSide.get() === LogicalSide.CLIENT) {
            BaseScreen.executeLater(GridScreen::class.java) { grid: GridScreen -> grid.view.sort() }
        }
    }

    private fun addFilter(filter: ItemStack) {
        val compare = FilterItem.getCompare(filter)
        val mode = FilterItem.getMode(filter)
        val modFilter = FilterItem.isModFilter(filter)
        val filters: MutableList<IFilter<*>> = ArrayList()
        val items = FilterItemsItemHandler(filter)
        for (stack in items.filteredItems) {
            if (stack.item === RSItems.FILTER) {
                addFilter(stack)
            } else if (!stack.isEmpty) {
                filters.add(ItemFilter(stack, compare, mode, modFilter))
            }
        }
        val fluids = FilterFluidInventory(filter)
        for (stack in fluids.filteredFluids) {
            filters.add(FluidFilter(stack, compare, mode, modFilter))
        }
        val icon = FilterItem.getIcon(filter)
        val fluidIcon: FluidInstance = FilterItem.getFluidIcon(filter)
        if (icon.isEmpty && fluidIcon.isEmpty()) {
            this.filters.addAll(filters)
        } else {
            tabs.add(GridTab(filters, FilterItem.getName(filter), icon, fluidIcon))
        }
    }

    init {
        addValidator(ItemValidator(RSItems.FILTER))
    }
}