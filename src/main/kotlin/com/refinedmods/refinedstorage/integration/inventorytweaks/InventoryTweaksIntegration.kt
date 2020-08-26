package com.refinedmods.refinedstorage.integration.inventorytweaks

import net.minecraftforge.fml.ModList

object InventoryTweaksIntegration {
    val isLoaded: Boolean
        get() = ModList.get().isLoaded("inventorytweaks")
}