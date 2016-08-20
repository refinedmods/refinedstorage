package refinedstorage.integration.ic2;

import net.minecraftforge.fml.common.Loader;

public final class IntegrationIC2 {
    public static boolean isLoaded() {
        return Loader.isModLoaded("IC2");
    }

    public static int toRS(double amount) {
        return amount >= Double.POSITIVE_INFINITY ? Integer.MAX_VALUE : ((int) Math.floor(amount) * 4);
    }

    public static double toEU(int amount) {
        return Math.floor(amount / 4);
    }
}
