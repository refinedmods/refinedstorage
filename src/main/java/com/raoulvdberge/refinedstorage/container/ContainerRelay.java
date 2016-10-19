package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.tile.TileRelay;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerRelay extends ContainerBase {
    public ContainerRelay(TileRelay relay, EntityPlayer player) {
        super(relay, player);

        addPlayerInventory(8, 50);
    }
}
