package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.RSContainers;
import com.raoulvdberge.refinedstorage.tile.TileSecurityManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerSecurityManager extends ContainerBase {
    public ContainerSecurityManager(TileSecurityManager securityManager, PlayerEntity player, int windowId) {
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

        transferManager.addBiTransfer(player.inventory, securityManager.getNode().getCardsItems());
        transferManager.addTransfer(securityManager.getNode().getEditCard(), player.inventory);
    }
}
