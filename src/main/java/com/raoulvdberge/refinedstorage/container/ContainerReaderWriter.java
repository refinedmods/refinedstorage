package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.tile.TileBase;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerReaderWriter extends ContainerBase {
    public ContainerReaderWriter(TileBase tile, EntityPlayer player) {
        super(tile, player);

        addPlayerInventory(8, 127);
    }
}
