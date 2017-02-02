package com.raoulvdberge.refinedstorage.integration.cyclopscore;

import net.minecraftforge.fml.common.Loader;

public final class IntegrationCyclopsCore {
    public static boolean isLoaded() {
        return Loader.isModLoaded("cyclopscore") && Loader.isModLoaded("commoncapabilities");
    }
}
