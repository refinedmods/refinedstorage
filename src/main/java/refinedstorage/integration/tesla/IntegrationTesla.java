package refinedstorage.integration.tesla;

import net.minecraftforge.fml.common.Loader;

public final class IntegrationTesla {
    public static boolean isLoaded() {
        return Loader.isModLoaded("tesla");
    }
}
