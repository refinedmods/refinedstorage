package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReader;

public class TileReader extends TileNode implements IReader {
    @Override
    public int getEnergyUsage() {
        return 0; // @TODO
    }

    @Override
    public void updateNode() {
    }

    @Override
    public int getRedstoneStrength() {
        return worldObj.getRedstonePower(pos, getDirection().getOpposite());
    }
}
