package com.raoulvdberge.refinedstorage.inventory;

import net.minecraft.tileentity.TileEntity;

public class ItemHandlerChangeListenerTile implements IItemHandlerChangeListener {
    private TileEntity tile;

    public ItemHandlerChangeListenerTile(TileEntity tile) {
        this.tile = tile;
    }

    @Override
    public void onChanged(int slot) {
        tile.markDirty();
    }
}
