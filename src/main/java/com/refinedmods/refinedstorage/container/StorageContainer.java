package com.refinedmods.refinedstorage.container;

import com.refinedmods.refinedstorage.RSContainers;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.blockentity.StorageBlockEntity;
import net.minecraft.world.entity.player.Player;

public class StorageContainer extends BaseContainer {
    public StorageContainer(StorageBlockEntity storage, Player player, int windowId) {
        super(RSContainers.STORAGE_BLOCK, storage, player, windowId);

        for (int i = 0; i < 9; ++i) {
            addSlot(new FilterSlot(storage.getNode().getFilters(), i, 8 + (18 * i), 20));
        }

        addPlayerInventory(8, 141);

        transferManager.addItemFilterTransfer(player.getInventory(), storage.getNode().getFilters());
    }
}
