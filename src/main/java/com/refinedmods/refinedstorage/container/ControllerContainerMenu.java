package com.refinedmods.refinedstorage.container;

import com.refinedmods.refinedstorage.RSContainerMenus;
import com.refinedmods.refinedstorage.blockentity.ControllerBlockEntity;
import net.minecraft.world.entity.player.Player;

public class ControllerContainerMenu extends BaseContainerMenu {
    public ControllerContainerMenu(ControllerBlockEntity controller, Player player, int windowId) {
        super(RSContainerMenus.CONTROLLER.get(), controller, player, windowId);

        addPlayerInventory(8, 99);
    }
}
