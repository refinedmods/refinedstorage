package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.RSContainers;
import com.raoulvdberge.refinedstorage.container.slot.filter.FilterSlot;
import com.raoulvdberge.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.raoulvdberge.refinedstorage.tile.DetectorTile;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import net.minecraft.entity.player.PlayerEntity;

public class DetectorContainer extends BaseContainer {
    public DetectorContainer(DetectorTile detector, PlayerEntity player, int windowId) {
        super(RSContainers.DETECTOR, detector, player, windowId);

        addSlot(new FilterSlot(detector.getNode().getItemFilters(), 0, 107, 20).setEnableHandler(() -> detector.getNode().getType() == IType.ITEMS));
        addSlot(new FluidFilterSlot(detector.getNode().getFluidFilters(), 0, 107, 20).setEnableHandler(() -> detector.getNode().getType() == IType.FLUIDS));

        addPlayerInventory(8, 55);

        transferManager.addFilterTransfer(player.inventory, detector.getNode().getItemFilters(), detector.getNode().getFluidFilters(), detector.getNode()::getType);
    }
}
