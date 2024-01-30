package com.refinedmods.refinedstorage.integration.jei;

import net.neoforged.fml.ModList;

public final class JeiIntegration {
    private JeiIntegration() {
    }

    public static boolean isLoaded() {
        return ModList.get().isLoaded("jei");
    }
}

