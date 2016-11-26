package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.RS;

public class TileCable extends TileNode {
    @Override
    public int getEnergyUsage() {
        return RS.INSTANCE.config.cableUsage;
    }

    @Override
    public void updateNode() {
        // NO OP
    }
}
