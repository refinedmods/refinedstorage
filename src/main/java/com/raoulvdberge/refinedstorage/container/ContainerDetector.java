package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.RSContainers;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilter;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilterFluid;
import com.raoulvdberge.refinedstorage.tile.TileDetector;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import net.minecraft.entity.player.PlayerEntity;

public class ContainerDetector extends ContainerBase {
    public ContainerDetector(TileDetector detector, PlayerEntity player, int windowId) {
        super(RSContainers.DETECTOR, detector, player, windowId);

        addSlot(new SlotFilter(detector.getNode().getItemFilters(), 0, 107, 20).setEnableHandler(() -> detector.getNode().getType() == IType.ITEMS));
        addSlot(new SlotFilterFluid(detector.getNode().getFluidFilters(), 0, 107, 20).setEnableHandler(() -> detector.getNode().getType() == IType.FLUIDS));

        addPlayerInventory(8, 55);

        transferManager.addFilterTransfer(player.inventory, detector.getNode().getItemFilters(), detector.getNode().getFluidFilters(), detector.getNode()::getType);
    }
}
