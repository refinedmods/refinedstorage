package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.RSContainers;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilter;
import com.raoulvdberge.refinedstorage.tile.TileStorage;
import net.minecraft.entity.player.PlayerEntity;

public class ContainerStorage extends ContainerBase {
    public ContainerStorage(TileStorage storage, PlayerEntity player, int windowId) {
        super(RSContainers.STORAGE, storage, player, windowId);

        for (int i = 0; i < 9; ++i) {
            addSlot(new SlotFilter(storage.getNode().getFilters(), i, 8 + (18 * i), 20));
        }

        addPlayerInventory(8, 141);

        transferManager.addItemFilterTransfer(player.inventory, storage.getNode().getFilters());
    }
}
