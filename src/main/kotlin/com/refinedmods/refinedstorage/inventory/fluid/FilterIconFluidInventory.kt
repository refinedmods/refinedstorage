package com.refinedmods.refinedstorage.inventory.fluid

import com.refinedmods.refinedstorage.item.FilterItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraftforge.fluids.FluidInstance

class FilterIconFluidInventory(stack: ItemStack) : FluidInventory(1, Int.MAX_VALUE) {
    init {
        addListener { handler: FluidInventory?, slot: Int, reading: Boolean ->
            if (!stack.hasTag()) {
                stack.tag = CompoundTag()
            }
            FilterItem.setFluidIcon(stack, getFluid(slot))
        }
        val icon: FluidInstance = FilterItem.getFluidIcon(stack)
        if (!icon.isEmpty()) {
            setFluid(0, icon)
        }
    }
}