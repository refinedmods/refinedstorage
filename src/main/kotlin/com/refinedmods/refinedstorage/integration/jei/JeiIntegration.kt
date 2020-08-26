package com.refinedmods.refinedstorage.integration.jei

import net.minecraftforge.fml.ModList

object JeiIntegration {
    val isLoaded: Boolean
        get() = ModList.get().isLoaded("jei")
}