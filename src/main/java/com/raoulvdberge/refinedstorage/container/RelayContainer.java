package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.RSContainers;
import com.raoulvdberge.refinedstorage.tile.TileRelay;
import net.minecraft.entity.player.PlayerEntity;

public class RelayContainer extends BaseContainer {
    public RelayContainer(TileRelay relay, PlayerEntity player, int windowId) {
        super(RSContainers.RELAY, relay, player, windowId);

        addPlayerInventory(8, 50);
    }
}
