package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.tile.TileController;
import net.minecraft.entity.player.PlayerEntity;

public class ContainerController extends ContainerBase {
    public ContainerController(TileController controller, PlayerEntity player) {
        super(controller, player);

        addPlayerInventory(8, 99);
    }
}
