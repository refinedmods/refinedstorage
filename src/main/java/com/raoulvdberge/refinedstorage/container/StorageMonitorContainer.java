package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.RSContainers;
import com.raoulvdberge.refinedstorage.container.slot.filter.FilterSlot;
import com.raoulvdberge.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.raoulvdberge.refinedstorage.tile.StorageMonitorTile;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import net.minecraft.entity.player.PlayerEntity;

public class StorageMonitorContainer extends BaseContainer {
    public StorageMonitorContainer(StorageMonitorTile storageMonitor, PlayerEntity player, int windowId) {
        super(RSContainers.STORAGE_MONITOR, storageMonitor, player, windowId);

        addSlot(new FilterSlot(storageMonitor.getNode().getItemFilters(), 0, 80, 20).setEnableHandler(() -> storageMonitor.getNode().getType() == IType.ITEMS));
        addSlot(new FluidFilterSlot(storageMonitor.getNode().getFluidFilters(), 0, 80, 20).setEnableHandler(() -> storageMonitor.getNode().getType() == IType.FLUIDS));

        addPlayerInventory(8, 55);

        transferManager.addFilterTransfer(player.inventory, storageMonitor.getNode().getItemFilters(), storageMonitor.getNode().getFluidFilters(), storageMonitor.getNode()::getType);
    }
}
