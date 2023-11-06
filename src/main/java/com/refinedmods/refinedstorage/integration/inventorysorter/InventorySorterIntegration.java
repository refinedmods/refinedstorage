package com.refinedmods.refinedstorage.integration.inventorysorter;

import com.refinedmods.refinedstorage.RS;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.InterModComms;

public class InventorySorterIntegration {
    private static final String ID = "inventorysorter";

    private InventorySorterIntegration() {
    }

    public static boolean isLoaded() {
        return ModList.get().isLoaded(ID);
    }

    public static void register() {
        // Prevent items moving while scrolling through slots with Inventory Sorter in the Crafter Manager
        InterModComms.sendTo("inventorysorter", "slotblacklist", () -> "com.refinedmods.refinedstorage.container.slot.CrafterManagerSlot");
        InterModComms.sendTo("inventorysorter", "containerblacklist", () -> new ResourceLocation(RS.ID, "crafter"));
    }
}
