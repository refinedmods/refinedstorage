package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.RSContainers;
import com.raoulvdberge.refinedstorage.tile.ControllerTile;
import net.minecraft.entity.player.PlayerEntity;

public class ControllerContainer extends BaseContainer {
    public ControllerContainer(ControllerTile controller, PlayerEntity player, int windowId) {
        super(RSContainers.CONTROLLER, controller, player, windowId);

        addPlayerInventory(8, 99);
    }
}
