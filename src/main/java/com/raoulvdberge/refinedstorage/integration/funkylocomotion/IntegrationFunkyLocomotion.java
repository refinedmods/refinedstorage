package com.raoulvdberge.refinedstorage.integration.funkylocomotion;

import com.rwtema.funkylocomotion.api.FunkyRegistry;

public final class IntegrationFunkyLocomotion {
    public static boolean isLoaded() {
        return FunkyRegistry.INSTANCE != null;
    }
}
