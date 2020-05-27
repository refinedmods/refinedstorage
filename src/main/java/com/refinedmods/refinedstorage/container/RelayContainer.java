package com.refinedmods.refinedstorage.container;

import com.refinedmods.refinedstorage.RSContainers;
import com.refinedmods.refinedstorage.tile.RelayTile;
import net.minecraft.entity.player.PlayerEntity;

public class RelayContainer extends BaseContainer {
    public RelayContainer(RelayTile relay, PlayerEntity player, int windowId) {
        super(RSContainers.RELAY, relay, player, windowId);

        addPlayerInventory(8, 50);
    }
}
