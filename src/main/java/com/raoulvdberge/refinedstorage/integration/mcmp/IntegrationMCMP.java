package com.raoulvdberge.refinedstorage.integration.mcmp;

import net.minecraftforge.fml.common.Loader;

public final class IntegrationMCMP {
    public static boolean isLoaded() {
        return Loader.isModLoaded("mcmultipart");
    }
}
