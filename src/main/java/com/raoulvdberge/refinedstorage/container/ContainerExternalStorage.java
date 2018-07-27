package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilter;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilterFluid;
import com.raoulvdberge.refinedstorage.tile.TileExternalStorage;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerExternalStorage extends ContainerBase {
    public ContainerExternalStorage(TileExternalStorage externalStorage, EntityPlayer player) {
        super(externalStorage, player);

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotFilter(externalStorage.getNode().getItemFilters(), i, 8 + (18 * i), 20).setEnableHandler(() -> externalStorage.getNode().getType() == IType.ITEMS));
        }

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotFilterFluid(externalStorage.getNode().getFluidFilters(), i, 8 + (18 * i), 20).setEnableHandler(() -> externalStorage.getNode().getType() == IType.FLUIDS));
        }

        addPlayerInventory(8, 141);

        transferManager.addFilterTransfer(player.inventory, externalStorage.getNode().getItemFilters(), externalStorage.getNode().getFluidFilters(), externalStorage.getNode()::getType);
    }
}
