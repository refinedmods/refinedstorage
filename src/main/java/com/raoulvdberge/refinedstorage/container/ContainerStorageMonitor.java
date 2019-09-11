package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.RSContainers;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilter;
import com.raoulvdberge.refinedstorage.tile.TileStorageMonitor;
import net.minecraft.entity.player.PlayerEntity;

public class ContainerStorageMonitor extends ContainerBase {
    public ContainerStorageMonitor(TileStorageMonitor storageMonitor, PlayerEntity player, int windowId) {
        super(RSContainers.STORAGE_MONITOR, storageMonitor, player, windowId);

        addSlot(new SlotFilter(storageMonitor.getNode().getItemFilters(), 0, 80, 20));

        addPlayerInventory(8, 55);

        transferManager.addItemFilterTransfer(player.inventory, storageMonitor.getNode().getItemFilters());
    }
}
