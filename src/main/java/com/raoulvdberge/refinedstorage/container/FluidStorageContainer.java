package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.RSContainers;
import com.raoulvdberge.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.raoulvdberge.refinedstorage.tile.FluidStorageTile;
import net.minecraft.entity.player.PlayerEntity;

public class FluidStorageContainer extends BaseContainer {
    public FluidStorageContainer(FluidStorageTile fluidStorage, PlayerEntity player, int windowId) {
        super(RSContainers.FLUID_STORAGE_BLOCK, fluidStorage, player, windowId);

        for (int i = 0; i < 9; ++i) {
            addSlot(new FluidFilterSlot(fluidStorage.getNode().getFilters(), i, 8 + (18 * i), 20));
        }

        addPlayerInventory(8, 141);

        transferManager.addFluidFilterTransfer(player.inventory, fluidStorage.getNode().getFilters());
    }
}
