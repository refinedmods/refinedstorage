package com.refinedmods.refinedstorage.container;

import com.refinedmods.refinedstorage.RSContainerMenus;
import com.refinedmods.refinedstorage.api.network.security.Permission;
import com.refinedmods.refinedstorage.blockentity.SecurityManagerBlockEntity;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.items.SlotItemHandler;

public class SecurityManagerContainerMenu extends BaseContainerMenu {
    private final SecurityManagerBlockEntity securityManager;

    public SecurityManagerContainerMenu(SecurityManagerBlockEntity securityManager, Player player, int windowId) {
        super(RSContainerMenus.SECURITY_MANAGER.get(), securityManager, player, windowId);

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

        this.securityManager = securityManager;
    }

    public void updatePermission(Permission permission, boolean state) {
        securityManager.getNode().updatePermission(permission, state);
    }
}
