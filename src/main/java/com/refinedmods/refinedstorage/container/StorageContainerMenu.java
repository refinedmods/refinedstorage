package com.refinedmods.refinedstorage.container;

import com.refinedmods.refinedstorage.RSContainerMenus;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.blockentity.StorageBlockEntity;
import net.minecraft.world.entity.player.Player;

public class StorageContainerMenu extends BaseContainerMenu {
    public StorageContainerMenu(StorageBlockEntity storage, Player player, int windowId) {
        super(RSContainerMenus.STORAGE_BLOCK.get(), storage, player, windowId);

        for (int i = 0; i < 9; ++i) {
            addSlot(new FilterSlot(storage.getNode().getFilters(), i, 8 + (18 * i), 20));
        }

        addPlayerInventory(8, 141);

        transferManager.addItemFilterTransfer(player.getInventory(), storage.getNode().getFilters());
    }
}
