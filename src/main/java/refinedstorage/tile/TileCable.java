package refinedstorage.tile;

import net.minecraft.inventory.Container;
import refinedstorage.RefinedStorage;

public class TileCable extends TileSlave {
    @Override
    public int getEnergyUsage() {
        return RefinedStorage.INSTANCE.cableRfUsage;
    }

    @Override
    public void updateSlave() {
        // NO OP
    }

    @Override
    public boolean canSendConnectivityUpdate() {
        return false;
    }

    @Override
    public Class<? extends Container> getContainer() {
        return null;
    }
}
