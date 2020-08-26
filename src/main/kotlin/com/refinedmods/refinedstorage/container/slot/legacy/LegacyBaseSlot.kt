package com.refinedmods.refinedstorage.container.slot.legacy

import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.container.Slot
import java.util.function.Supplier

open class LegacyBaseSlot(inventory: IInventory?, inventoryIndex: Int, x: Int, y: Int) : Slot(inventory, inventoryIndex, x, y) {
    private var enableHandler = Supplier { true }
    fun setEnableHandler(enableHandler: Supplier<Boolean>): LegacyBaseSlot {
        this.enableHandler = enableHandler
        return this
    }

    val isEnabled: Boolean
        get() = enableHandler.get()
}