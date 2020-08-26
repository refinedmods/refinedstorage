package com.refinedmods.refinedstorage.container

import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.container.slot.DisabledSlot
import com.refinedmods.refinedstorage.container.slot.filter.DisabledFluidFilterSlot
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory
import com.refinedmods.refinedstorage.screen.grid.stack.FluidGridStack
import com.refinedmods.refinedstorage.screen.grid.stack.IGridStack
import com.refinedmods.refinedstorage.screen.grid.stack.ItemGridStack
import net.minecraft.entity.player.PlayerEntity
import net.minecraftforge.items.ItemHandlerHelper
import net.minecraftforge.items.ItemStackHandler

class CraftingSettingsContainer(player: PlayerEntity, stack: IGridStack?) : BaseContainer(null, null, player, 0) {
    init {
        if (stack is FluidGridStack) {
            val inventory = FluidInventory(1)
            inventory.setFluid(0, stack.stack)
            addSlot(DisabledFluidFilterSlot(inventory, 0, 89, 48))
        } else if (stack is ItemGridStack) {
            val handler = ItemStackHandler(1)
            handler.setStackInSlot(0, ItemHandlerHelper.copyStackWithSize(stack.stack, 1))
            addSlot(DisabledSlot(handler, 0, 89, 48))
        }
    }
}