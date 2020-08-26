package com.refinedmods.refinedstorage.container

import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import com.refinedmods.refinedstorage.container.slot.DisabledSlot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraftforge.items.ItemHandlerHelper
import net.minecraftforge.items.ItemStackHandler

class AmountContainer(player: PlayerEntity, stack: ItemStack?) : BaseContainer(null, null, player, 0) {
    init {
        val inventory = ItemStackHandler(1)
        inventory.setStackInSlot(0, ItemHandlerHelper.copyStackWithSize(stack, 1))
        addSlot(DisabledSlot(inventory, 0, 89, 48))
    }
}