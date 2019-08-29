package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilter;
import com.raoulvdberge.refinedstorage.tile.TileStorage;
import net.minecraft.entity.player.PlayerEntity;

public class ContainerStorage extends ContainerBase {
    public ContainerStorage(TileStorage storage, PlayerEntity player) {
        super(storage, player);

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotFilter(storage.getNode().getFilters(), i, 8 + (18 * i), 20));
        }

        addPlayerInventory(8, 141);

        transferManager.addItemFilterTransfer(player.inventory, storage.getNode().getFilters());
    }
}
