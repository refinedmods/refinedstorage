package com.raoulvdberge.refinedstorage.integration.inventorysorter;

import net.minecraftforge.fml.common.event.FMLInterModComms;

public class IntegrationInventorySorter {
    public static void register() {
        // Prevent items moving while scrolling through slots with Inventory Sorter in the Crafter Manager
        FMLInterModComms.sendMessage("inventorysorter", "containerblacklist", "com.raoulvdberge.refinedstorage.container.ContainerCrafterManager");
    }
}
