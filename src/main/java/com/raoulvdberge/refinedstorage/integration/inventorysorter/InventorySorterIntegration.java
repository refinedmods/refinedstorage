package com.raoulvdberge.refinedstorage.integration.inventorysorter;

import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;

public class InventorySorterIntegration {
    private static final String ID = "inventorysorter";

    public static boolean isLoaded() {
        return ModList.get().isLoaded(ID);
    }

    public static void register() {
        // Prevent items moving while scrolling through slots with Inventory Sorter in the Crafter Manager
        InterModComms.sendTo("inventorysorter", "slotblacklist", () -> "com.raoulvdberge.refinedstorage.container.slot.CrafterManagerSlot");
    }
}
