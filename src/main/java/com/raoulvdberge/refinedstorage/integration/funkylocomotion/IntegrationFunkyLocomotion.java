package com.raoulvdberge.refinedstorage.integration.funkylocomotion;

import net.minecraftforge.fml.common.Loader;

public final class IntegrationFunkyLocomotion {
    public static boolean isLoaded() {
        return Loader.isModLoaded("funkylocomotion");
    }
}
