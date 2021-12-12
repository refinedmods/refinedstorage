package com.refinedmods.refinedstorage.container;

import com.refinedmods.refinedstorage.RSContainers;
import com.refinedmods.refinedstorage.tile.ControllerTile;
import net.minecraft.world.entity.player.Player;

public class ControllerContainer extends BaseContainer {
    public ControllerContainer(ControllerTile controller, Player player, int windowId) {
        super(RSContainers.CONTROLLER, controller, player, windowId);

        addPlayerInventory(8, 99);
    }
}
