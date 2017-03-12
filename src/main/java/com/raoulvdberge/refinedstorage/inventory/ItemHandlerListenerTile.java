package com.raoulvdberge.refinedstorage.inventory;

import net.minecraft.tileentity.TileEntity;

import java.util.function.Consumer;

public class ItemHandlerListenerTile implements Consumer<Integer> {
    private TileEntity tile;

    public ItemHandlerListenerTile(TileEntity tile) {
        this.tile = tile;
    }

    @Override
    public void accept(Integer slot) {
        tile.markDirty();
    }
}
