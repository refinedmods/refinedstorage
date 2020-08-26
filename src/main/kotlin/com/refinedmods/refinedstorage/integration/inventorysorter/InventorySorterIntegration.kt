package com.refinedmods.refinedstorage.integration.inventorysorter

import net.minecraftforge.fml.ModList

object InventorySorterIntegration {
    private const val ID = "inventorysorter"
    val isLoaded: Boolean
        get() = ModList.get().isLoaded(ID)

    @JvmStatic
    fun register() {
        // Prevent items moving while scrolling through slots with Inventory Sorter in the Crafter Manager
        // InterModComms.sendTo("inventorysorter", "slotblacklist", () -> "com.refinedmods.refinedstorage.container.slot.CrafterManagerSlot");
    }
}