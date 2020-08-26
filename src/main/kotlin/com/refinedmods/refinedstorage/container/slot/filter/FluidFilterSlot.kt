package com.refinedmods.refinedstorage.container.slot.filter

import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.container.slot.BaseSlot
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory
import com.refinedmods.refinedstorage.util.StackUtils.getFluid
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraftforge.items.ItemStackHandler

open class FluidFilterSlot @JvmOverloads constructor(val fluidInventory: FluidInventory, inventoryIndex: Int, x: Int, y: Int, private val flags: Int = 0) : BaseSlot(ItemStackHandler(fluidInventory.slots), inventoryIndex, x, y) {
    fun isItemValid(@Nonnull stack: ItemStack?): Boolean {
        return false
    }

    open fun onContainerClicked(@Nonnull stack: ItemStack?) {
        fluidInventory.setFluid(getSlotIndex(), getFluid(stack!!, true).value)
    }

    fun canTakeStack(playerIn: PlayerEntity?): Boolean {
        return false
    }

    val isSizeAllowed: Boolean
        get() = flags and FILTER_ALLOW_SIZE == FILTER_ALLOW_SIZE
    val isAlternativesAllowed: Boolean
        get() = flags and FILTER_ALLOW_ALTERNATIVES == FILTER_ALLOW_ALTERNATIVES

    companion object {
        const val FILTER_ALLOW_SIZE = 1
        const val FILTER_ALLOW_ALTERNATIVES = 2
    }
}