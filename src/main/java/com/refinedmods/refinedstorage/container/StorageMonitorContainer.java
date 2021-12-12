package com.refinedmods.refinedstorage.container;

import com.refinedmods.refinedstorage.RSContainers;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.refinedmods.refinedstorage.tile.StorageMonitorTile;
import com.refinedmods.refinedstorage.tile.config.IType;
import net.minecraft.world.entity.player.Player;

public class StorageMonitorContainer extends BaseContainer {
    public StorageMonitorContainer(StorageMonitorTile storageMonitor, Player player, int windowId) {
        super(RSContainers.STORAGE_MONITOR, storageMonitor, player, windowId);

        addSlot(new FilterSlot(storageMonitor.getNode().getItemFilters(), 0, 80, 20).setEnableHandler(() -> storageMonitor.getNode().getType() == IType.ITEMS));
        addSlot(new FluidFilterSlot(storageMonitor.getNode().getFluidFilters(), 0, 80, 20).setEnableHandler(() -> storageMonitor.getNode().getType() == IType.FLUIDS));

        addPlayerInventory(8, 55);

        transferManager.addFilterTransfer(player.getInventory(), storageMonitor.getNode().getItemFilters(), storageMonitor.getNode().getFluidFilters(), storageMonitor.getNode()::getType);
    }
}
