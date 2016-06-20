package refinedstorage.tile;

import net.minecraft.inventory.Container;

public class TileCable extends TileSlave {
    @Override
    public int getEnergyUsage() {
        return 0;
    }

    @Override
    public void updateSlave() {
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
