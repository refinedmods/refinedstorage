package com.raoulvdberge.refinedstorage.inventory.listener;

import net.minecraft.tileentity.TileEntity;

import java.util.function.Consumer;

public class TileListener implements Consumer<Integer> {
    private TileEntity tile;

    public TileListener(TileEntity tile) {
        this.tile = tile;
    }

    @Override
    public void accept(Integer slot) {
        tile.markDirty();
    }
}
