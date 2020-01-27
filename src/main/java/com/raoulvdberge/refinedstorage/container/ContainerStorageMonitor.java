package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilter;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilterFluid;
import com.raoulvdberge.refinedstorage.tile.TileStorageMonitor;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerStorageMonitor extends ContainerBase {
    public ContainerStorageMonitor(TileStorageMonitor storageMonitor, EntityPlayer player) {
        super(storageMonitor, player);

        addSlotToContainer(new SlotFilter(storageMonitor.getNode().getItemFilters(), 0, 80, 20).setEnableHandler(() -> storageMonitor.getNode().getType() == IType.ITEMS));
        addSlotToContainer(new SlotFilterFluid(storageMonitor.getNode().getFluidFilters(), 0, 80, 20).setEnableHandler(() -> storageMonitor.getNode().getType() == IType.FLUIDS));

        addPlayerInventory(8, 55);

        transferManager.addFilterTransfer(player.inventory, storageMonitor.getNode().getItemFilters(), storageMonitor.getNode().getFluidFilters(), storageMonitor.getNode()::getType);
    }
}
