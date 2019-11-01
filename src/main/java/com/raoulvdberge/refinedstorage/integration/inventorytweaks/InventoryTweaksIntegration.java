package com.raoulvdberge.refinedstorage.integration.inventorytweaks;

import net.minecraftforge.fml.ModList;

public class InventoryTweaksIntegration {
    public static boolean isLoaded() {
        return ModList.get().isLoaded("inventorytweaks");
    }
}
