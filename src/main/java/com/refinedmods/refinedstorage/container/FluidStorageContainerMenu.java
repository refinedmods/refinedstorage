package com.refinedmods.refinedstorage.container;

import com.refinedmods.refinedstorage.RSContainerMenus;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.refinedmods.refinedstorage.blockentity.FluidStorageBlockEntity;
import net.minecraft.world.entity.player.Player;

public class FluidStorageContainerMenu extends BaseContainerMenu {
    public FluidStorageContainerMenu(FluidStorageBlockEntity fluidStorage, Player player, int windowId) {
        super(RSContainerMenus.FLUID_STORAGE_BLOCK.get(), fluidStorage, player, windowId);

        for (int i = 0; i < 9; ++i) {
            addSlot(new FluidFilterSlot(fluidStorage.getNode().getFilters(), i, 8 + (18 * i), 20));
        }

        addPlayerInventory(8, 141);

        transferManager.addFluidFilterTransfer(player.getInventory(), fluidStorage.getNode().getFilters());
    }
}
