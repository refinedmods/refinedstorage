package com.refinedmods.refinedstorage.inventory.listener

import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler
import net.minecraft.tileentity.BlockEntity

class TileInventoryListener(tile: BlockEntity) : InventoryListener<BaseItemHandler?> {
    private val tile: BlockEntity
    override fun onChanged(handler: BaseItemHandler?, slot: Int, reading: Boolean) {
        if (!reading) {
            tile.markDirty()
        }
    }

    init {
        this.tile = tile
    }
}