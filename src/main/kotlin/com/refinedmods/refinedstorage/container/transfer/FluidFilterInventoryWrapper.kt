package com.refinedmods.refinedstorage.container.transfer

import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.api.util.IComparer
import com.refinedmods.refinedstorage.apiimpl.API.Companion.instance
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory
import com.refinedmods.refinedstorage.util.StackUtils.copy
import com.refinedmods.refinedstorage.util.StackUtils.getFluid
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidAttributes
import net.minecraftforge.fluids.FluidInstance

internal class FluidFilterInventoryWrapper(private val filterInv: FluidInventory) : IInventoryWrapper {
    override fun insert(stack: ItemStack?): InsertionResult? {
        val stop = InsertionResult(InsertionResultType.STOP)
        val fluidInContainer: FluidInstance = getFluid(stack!!, true).value
        if (fluidInContainer.isEmpty()) {
            return stop
        }
        for (fluid in filterInv.fluids) {
            if (instance().getComparer().isEqual(fluidInContainer, fluid, IComparer.COMPARE_NBT)) {
                return stop
            }
        }
        for (i in 0 until filterInv.slots) {
            if (filterInv.getFluid(i).isEmpty()) {
                filterInv.setFluid(i, copy(fluidInContainer, FluidAttributes.BUCKET_VOLUME))
                return stop
            }
        }
        return stop
    }
}