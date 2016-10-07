package refinedstorage.tile;

import refinedstorage.RS;

public class TileCable extends TileMultipartNode {
    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.cableUsage;
    }

    @Override
    public void updateNode() {
        // NO OP
    }
}
