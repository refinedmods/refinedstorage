package com.raoulvdberge.refinedstorage.integration.jei;

import net.minecraftforge.fml.ModList;

public final class JeiIntegration {
    public static boolean isLoaded() {
        return ModList.get().isLoaded("jei");
    }
}

