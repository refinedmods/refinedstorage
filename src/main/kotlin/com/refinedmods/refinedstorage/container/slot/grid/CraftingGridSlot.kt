package com.refinedmods.refinedstorage.container.slot.grid

import com.refinedmods.refinedstorage.api.network.grid.IGrid.slotId
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.container.Slot

class CraftingGridSlot(inventory: IInventory?, inventoryIndex: Int, x: Int, y: Int) : Slot(inventory, inventoryIndex, x, y)