package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.RSContainers;
import com.raoulvdberge.refinedstorage.container.slot.filter.FilterSlot;
import com.raoulvdberge.refinedstorage.tile.TileStorage;
import net.minecraft.entity.player.PlayerEntity;

public class StorageContainer extends BaseContainer {
    public StorageContainer(TileStorage storage, PlayerEntity player, int windowId) {
        super(RSContainers.STORAGE, storage, player, windowId);

        for (int i = 0; i < 9; ++i) {
            addSlot(new FilterSlot(storage.getNode().getFilters(), i, 8 + (18 * i), 20));
        }

        addPlayerInventory(8, 141);

        transferManager.addItemFilterTransfer(player.inventory, storage.getNode().getFilters());
    }
}
