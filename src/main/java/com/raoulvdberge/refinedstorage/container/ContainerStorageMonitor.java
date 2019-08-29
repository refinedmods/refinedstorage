package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilter;
import com.raoulvdberge.refinedstorage.tile.TileStorageMonitor;
import net.minecraft.entity.player.PlayerEntity;

public class ContainerStorageMonitor extends ContainerBase {
    public ContainerStorageMonitor(TileStorageMonitor storageMonitor, PlayerEntity player) {
        super(storageMonitor, player);

        addSlotToContainer(new SlotFilter(storageMonitor.getNode().getItemFilters(), 0, 80, 20));

        addPlayerInventory(8, 55);

        transferManager.addItemFilterTransfer(player.inventory, storageMonitor.getNode().getItemFilters());
    }
}
