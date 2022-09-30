package com.refinedmods.refinedstorage.container;

import com.refinedmods.refinedstorage.RSContainerMenus;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.refinedmods.refinedstorage.blockentity.StorageMonitorBlockEntity;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import net.minecraft.world.entity.player.Player;

public class StorageMonitorContainerMenu extends BaseContainerMenu {
    public StorageMonitorContainerMenu(StorageMonitorBlockEntity storageMonitor, Player player, int windowId) {
        super(RSContainerMenus.STORAGE_MONITOR.get(), storageMonitor, player, windowId);

        addSlot(new FilterSlot(storageMonitor.getNode().getItemFilters(), 0, 80, 20).setEnableHandler(() -> storageMonitor.getNode().getType() == IType.ITEMS));
        addSlot(new FluidFilterSlot(storageMonitor.getNode().getFluidFilters(), 0, 80, 20).setEnableHandler(() -> storageMonitor.getNode().getType() == IType.FLUIDS));

        addPlayerInventory(8, 55);

        transferManager.addFilterTransfer(player.getInventory(), storageMonitor.getNode().getItemFilters(), storageMonitor.getNode().getFluidFilters(), storageMonitor.getNode()::getType);
    }
}
