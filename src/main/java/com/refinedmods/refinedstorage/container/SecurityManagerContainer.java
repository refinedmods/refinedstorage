package com.refinedmods.refinedstorage.container;

import com.refinedmods.refinedstorage.RSContainers;
import com.refinedmods.refinedstorage.tile.SecurityManagerTile;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.SlotItemHandler;

public class SecurityManagerContainer extends BaseContainer {
    public SecurityManagerContainer(SecurityManagerTile securityManager, Player player, int windowId) {
        super(RSContainers.SECURITY_MANAGER, securityManager, player, windowId);

        int x = 8;
        int y = 20;

        for (int i = 0; i < 9 * 2; ++i) {
            addSlot(new SlotItemHandler(securityManager.getNode().getCardsItems(), i, x, y));

            if (((i + 1) % 9) == 0) {
                x = 8;
                y += 18;
            } else {
                x += 18;
            }
        }

        addSlot(new SlotItemHandler(securityManager.getNode().getEditCard(), 0, 80, 70));

        addPlayerInventory(8, 152);

        transferManager.addBiTransfer(player.getInventory(), securityManager.getNode().getCardsItems());
        transferManager.addTransfer(securityManager.getNode().getEditCard(), player.getInventory());
    }
}
