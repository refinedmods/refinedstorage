package com.refinedmods.refinedstorage.container;

import com.refinedmods.refinedstorage.RSContainers;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.refinedmods.refinedstorage.tile.ExternalStorageTile;
import com.refinedmods.refinedstorage.tile.config.IType;
import net.minecraft.world.entity.player.Player;

public class ExternalStorageContainer extends BaseContainer {
    public ExternalStorageContainer(ExternalStorageTile externalStorage, Player player, int windowId) {
        super(RSContainers.EXTERNAL_STORAGE, externalStorage, player, windowId);

        for (int i = 0; i < 9; ++i) {
            addSlot(new FilterSlot(externalStorage.getNode().getItemFilters(), i, 8 + (18 * i), 20).setEnableHandler(() -> externalStorage.getNode().getType() == IType.ITEMS));
        }

        for (int i = 0; i < 9; ++i) {
            addSlot(new FluidFilterSlot(externalStorage.getNode().getFluidFilters(), i, 8 + (18 * i), 20).setEnableHandler(() -> externalStorage.getNode().getType() == IType.FLUIDS));
        }

        addPlayerInventory(8, 141);

        transferManager.addFilterTransfer(player.getInventory(), externalStorage.getNode().getItemFilters(), externalStorage.getNode().getFluidFilters(), externalStorage.getNode()::getType);
    }
}
