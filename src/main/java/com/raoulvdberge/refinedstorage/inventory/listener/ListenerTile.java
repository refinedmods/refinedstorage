package com.raoulvdberge.refinedstorage.inventory.listener;

import net.minecraft.tileentity.TileEntity;

import java.util.function.Consumer;

public class ListenerTile implements Consumer<Integer> {
    private TileEntity tile;

    public ListenerTile(TileEntity tile) {
        this.tile = tile;
    }

    @Override
    public void accept(Integer slot) {
        tile.markDirty();
    }
}
