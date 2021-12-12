package com.refinedmods.refinedstorage.inventory.listener;

import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import net.minecraft.tileentity.TileEntity;

public class TileInventoryListener implements InventoryListener<BaseItemHandler> {
    private final TileEntity tile;

    public TileInventoryListener(TileEntity tile) {
        this.tile = tile;
    }

    @Override
    public void onChanged(BaseItemHandler handler, int slot, boolean reading) {
        if (!reading) {
            tile.setChanged();
        }
    }
}
