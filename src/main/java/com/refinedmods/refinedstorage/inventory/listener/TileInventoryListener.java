package com.refinedmods.refinedstorage.inventory.listener;

import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import net.minecraft.world.level.block.entity.BlockEntity;

public class TileInventoryListener implements InventoryListener<BaseItemHandler> {
    private final BlockEntity tile;

    public TileInventoryListener(BlockEntity tile) {
        this.tile = tile;
    }

    @Override
    public void onChanged(BaseItemHandler handler, int slot, boolean reading) {
        if (!reading) {
            tile.setChanged();
        }
    }
}
