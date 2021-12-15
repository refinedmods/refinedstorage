package com.refinedmods.refinedstorage.container;

import com.refinedmods.refinedstorage.RSContainerMenus;
import com.refinedmods.refinedstorage.blockentity.RelayBlockEntity;
import net.minecraft.world.entity.player.Player;

public class RelayContainerMenu extends BaseContainerMenu {
    public RelayContainerMenu(RelayBlockEntity relay, Player player, int windowId) {
        super(RSContainerMenus.RELAY, relay, player, windowId);

        addPlayerInventory(8, 50);
    }
}
