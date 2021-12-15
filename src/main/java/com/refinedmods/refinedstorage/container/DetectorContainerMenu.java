package com.refinedmods.refinedstorage.container;

import com.refinedmods.refinedstorage.RSContainerMenus;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.refinedmods.refinedstorage.blockentity.DetectorBlockEntity;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import net.minecraft.world.entity.player.Player;

public class DetectorContainerMenu extends BaseContainerMenu {
    public DetectorContainerMenu(DetectorBlockEntity detector, Player player, int windowId) {
        super(RSContainerMenus.DETECTOR, detector, player, windowId);

        addSlot(new FilterSlot(detector.getNode().getItemFilters(), 0, 107, 20).setEnableHandler(() -> detector.getNode().getType() == IType.ITEMS));
        addSlot(new FluidFilterSlot(detector.getNode().getFluidFilters(), 0, 107, 20).setEnableHandler(() -> detector.getNode().getType() == IType.FLUIDS));

        addPlayerInventory(8, 55);

        transferManager.addFilterTransfer(player.getInventory(), detector.getNode().getItemFilters(), detector.getNode().getFluidFilters(), detector.getNode()::getType);
    }
}
