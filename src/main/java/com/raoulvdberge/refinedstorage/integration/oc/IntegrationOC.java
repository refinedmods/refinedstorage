package com.raoulvdberge.refinedstorage.integration.oc;

import net.minecraftforge.fml.common.Loader;

public final class IntegrationOC {
    private static final String ID = "opencomputers";

    public static boolean isLoaded() {
        return Loader.isModLoaded(ID);
    }
}
