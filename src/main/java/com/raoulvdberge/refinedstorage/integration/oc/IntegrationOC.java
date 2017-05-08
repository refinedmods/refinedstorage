package com.raoulvdberge.refinedstorage.integration.oc;

import li.cil.oc.api.Driver;
import net.minecraftforge.fml.common.Loader;

public final class IntegrationOC {
    private static final String ID = "opencomputers";

    public static boolean isLoaded() {
        return Loader.isModLoaded(ID);
    }

    public static void register() {
        Driver.add(new DriverNetwork());
    }
}
