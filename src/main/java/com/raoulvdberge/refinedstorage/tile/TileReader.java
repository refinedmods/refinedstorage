package com.raoulvdberge.refinedstorage.tile;

import com.raoulvdberge.refinedstorage.api.network.readerwriter.IReader;

public class TileReader extends TileNode implements IReader, IReaderWriterGui {
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

    @Override
    public String getTitle() {
        return "gui.refinedstorage:reader";
    }

    @Override
    public void onAdd(String name) {
        // @TODO
    }

    @Override
    public void onRemove(String name) {
        // @TODO
    }
}
