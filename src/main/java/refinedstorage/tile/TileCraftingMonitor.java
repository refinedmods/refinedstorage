package refinedstorage.tile;

import refinedstorage.RefinedStorage;

public class TileCraftingMonitor extends TileNode {
    @Override
    public int getEnergyUsage() {
        return RefinedStorage.INSTANCE.config.craftingMonitorUsage;
    }

    @Override
    public void updateNode() {
    }

    @Override
    public boolean hasConnectivityState() {
        return true;
    }
}
