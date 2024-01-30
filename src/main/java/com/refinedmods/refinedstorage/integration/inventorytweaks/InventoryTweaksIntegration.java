package com.refinedmods.refinedstorage.integration.inventorytweaks;

import net.neoforged.fml.ModList;

public class InventoryTweaksIntegration {
    private InventoryTweaksIntegration() {
    }

    public static boolean isLoaded() {
        return ModList.get().isLoaded("inventorytweaks");
    }
}
