package com.refinedmods.refinedstorage.container.slot

import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler
import java.util.function.Supplier

open class BaseSlot(itemHandler: IItemHandler?, inventoryIndex: Int, x: Int, y: Int) : SlotItemHandler(itemHandler, inventoryIndex, x, y) {
    private var enableHandler = Supplier { true }
    fun setEnableHandler(enableHandler: Supplier<Boolean>): BaseSlot {
        this.enableHandler = enableHandler
        return this
    }

    open val isEnabled: Boolean
        get() = enableHandler.get()
}