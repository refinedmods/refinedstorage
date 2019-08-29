package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilterFluid;
import com.raoulvdberge.refinedstorage.tile.TileFluidStorage;
import net.minecraft.entity.player.PlayerEntity;

public class ContainerFluidStorage extends ContainerBase {
    public ContainerFluidStorage(TileFluidStorage fluidStorage, PlayerEntity player) {
        super(fluidStorage, player);

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotFilterFluid(fluidStorage.getNode().getFilters(), i, 8 + (18 * i), 20));
        }

        addPlayerInventory(8, 141);

        transferManager.addFluidFilterTransfer(player.inventory, fluidStorage.getNode().getFilters());
    }
}
