package com.refinedmods.refinedstorage.container

import com.refinedmods.refinedstorage.RSContainers
import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot
import com.refinedmods.refinedstorage.inventory.fluid.FilterFluidInventory
import com.refinedmods.refinedstorage.inventory.fluid.FilterIconFluidInventory
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory
import com.refinedmods.refinedstorage.inventory.item.FilterIconItemHandler
import com.refinedmods.refinedstorage.inventory.item.FilterItemsItemHandler
import com.refinedmods.refinedstorage.item.FilterItem
import com.refinedmods.refinedstorage.tile.config.IType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import java.util.function.Supplier

class FilterContainer(player: PlayerEntity, val stack: ItemStack, windowId: Int) : BaseContainer(RSContainers.FILTER, null, player, windowId) {
    protected override val disabledSlotNumber: Int
        protected get() = player.inventory.currentItem

    init {
        var y = 20
        var x = 8
        val filter = FilterItemsItemHandler(stack)
        val fluidFilter: FluidInventory = FilterFluidInventory(stack)
        for (i in 0..26) {
            addSlot(FilterSlot(filter, i, x, y).setEnableHandler { FilterItem.getType(stack) == IType.ITEMS })
            addSlot(FluidFilterSlot(fluidFilter, i, x, y).setEnableHandler { FilterItem.getType(stack) == IType.FLUIDS })
            if ((i + 1) % 9 == 0) {
                x = 8
                y += 18
            } else {
                x += 18
            }
        }
        addSlot(FilterSlot(FilterIconItemHandler(stack), 0, 8, 117).setEnableHandler { FilterItem.getType(stack) == IType.ITEMS })
        addSlot(FluidFilterSlot(FilterIconFluidInventory(stack), 0, 8, 117).setEnableHandler { FilterItem.getType(stack) == IType.FLUIDS })
        addPlayerInventory(8, 149)
        transferManager.addFilterTransfer(player.inventory, filter, fluidFilter, Supplier { FilterItem.getType(stack) })
    }
}