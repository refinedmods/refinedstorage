package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.RSContainers;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilter;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilterFluid;
import com.raoulvdberge.refinedstorage.tile.TileExternalStorage;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import net.minecraft.entity.player.PlayerEntity;

public class ContainerExternalStorage extends ContainerBase {
    public ContainerExternalStorage(TileExternalStorage externalStorage, PlayerEntity player, int windowId) {
        super(RSContainers.EXTERNAL_STORAGE, externalStorage, player, windowId);

        for (int i = 0; i < 9; ++i) {
            addSlot(new SlotFilter(externalStorage.getNode().getItemFilters(), i, 8 + (18 * i), 20).setEnableHandler(() -> externalStorage.getNode().getType() == IType.ITEMS));
        }

        for (int i = 0; i < 9; ++i) {
            addSlot(new SlotFilterFluid(externalStorage.getNode().getFluidFilters(), i, 8 + (18 * i), 20).setEnableHandler(() -> externalStorage.getNode().getType() == IType.FLUIDS));
        }

        addPlayerInventory(8, 141);

        transferManager.addFilterTransfer(player.inventory, externalStorage.getNode().getItemFilters(), externalStorage.getNode().getFluidFilters(), externalStorage.getNode()::getType);
    }
}
