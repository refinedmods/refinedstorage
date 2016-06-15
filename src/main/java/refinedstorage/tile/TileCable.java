package refinedstorage.tile;

import net.minecraft.inventory.Container;

public class TileCable extends TileMachine {
    @Override
    public int getEnergyUsage() {
        return 0;
    }

    @Override
    public void updateMachine() {
    }

    @Override
    public Class<? extends Container> getContainer() {
        return null;
    }

    public boolean canSendConnectivityData() {
        return false;
    }
}
