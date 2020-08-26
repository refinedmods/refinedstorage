package com.refinedmods.refinedstorage.container.transfer

import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory
import com.refinedmods.refinedstorage.tile.config.IType
import net.minecraft.item.ItemStack
import net.minecraftforge.items.IItemHandlerModifiable
import java.util.function.Supplier

internal class FilterInventoryWrapper(itemTo: IItemHandlerModifiable, fluidTo: FluidInventory, typeGetter: Supplier<Int>) : IInventoryWrapper {
    private val item: ItemFilterInventoryWrapper
    private val fluid: FluidFilterInventoryWrapper
    private val typeGetter: Supplier<Int>
    override fun insert(stack: ItemStack?): InsertionResult? {
        return if (typeGetter.get() == IType.ITEMS) item.insert(stack) else fluid.insert(stack)
    }

    init {
        item = ItemFilterInventoryWrapper(itemTo)
        fluid = FluidFilterInventoryWrapper(fluidTo)
        this.typeGetter = typeGetter
    }
}