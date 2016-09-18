package refinedstorage.tile;

import refinedstorage.RefinedStorage;

public class TileCable extends TileMultipartNode {
    @Override
    public int getEnergyUsage() {
        return RefinedStorage.CONFIG.cableUsage;
    }

    @Override
    public void updateNode() {
        // NO OP
    }
}
