package com.refinedmods.refinedstorage.inventory.fluid

import com.refinedmods.refinedstorage.item.FilterItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.NonNullList
import net.minecraftforge.fluids.FluidInstance

class FilterFluidInventory(stack: ItemStack) : FluidInventory(27, Int.MAX_VALUE) {
    fun getFilteredFluids(): NonNullList<FluidInstance> {
        val list: NonNullList<FluidInstance> = NonNullList.create()
        for (fluid in fluids) {
            if (fluid != null) {
                list.add(fluid)
            }
        }
        return list
    }

    init {
        addListener { handler: FluidInventory?, slot: Int, reading: Boolean ->
            if (!stack.hasTag()) {
                stack.tag = CompoundTag()
            }
            stack.tag!!.put(FilterItem.NBT_FLUID_FILTERS, writeToNbt())
        }
        if (stack.hasTag() && stack.tag!!.contains(FilterItem.NBT_FLUID_FILTERS)) {
            readFromNbt(stack.tag!!.getCompound(FilterItem.NBT_FLUID_FILTERS))
        }
    }
}