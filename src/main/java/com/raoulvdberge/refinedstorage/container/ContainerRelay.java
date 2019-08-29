package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.tile.TileRelay;
import net.minecraft.entity.player.PlayerEntity;

public class ContainerRelay extends ContainerBase {
    public ContainerRelay(TileRelay relay, PlayerEntity player) {
        super(relay, player);

        addPlayerInventory(8, 50);
    }
}
