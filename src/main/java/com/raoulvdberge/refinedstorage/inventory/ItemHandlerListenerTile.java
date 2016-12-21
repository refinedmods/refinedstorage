package com.raoulvdberge.refinedstorage.inventory;

import net.minecraft.tileentity.TileEntity;

public class ItemHandlerListenerTile implements IItemHandlerListener {
    private TileEntity tile;

    public ItemHandlerListenerTile(TileEntity tile) {
        this.tile = tile;
    }

    @Override
    public void onChanged(int slot) {
        tile.markDirty();
    }
}
