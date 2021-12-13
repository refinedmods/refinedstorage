package com.refinedmods.refinedstorage.container;

import com.refinedmods.refinedstorage.RSContainers;
import com.refinedmods.refinedstorage.blockentity.RelayBlockEntity;
import net.minecraft.world.entity.player.Player;

public class RelayContainer extends BaseContainer {
    public RelayContainer(RelayBlockEntity relay, Player player, int windowId) {
        super(RSContainers.RELAY, relay, player, windowId);

        addPlayerInventory(8, 50);
    }
}
