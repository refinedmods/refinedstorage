package refinedstorage.tile;

import refinedstorage.RefinedStorage;

public class TileCable extends TileNode {
    @Override
    public int getEnergyUsage() {
        return RefinedStorage.INSTANCE.cableUsage;
    }

    @Override
    public void updateNode() {
        // NO OP
    }
}
