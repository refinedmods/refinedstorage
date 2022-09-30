package com.refinedmods.refinedstorage.container;

import com.refinedmods.refinedstorage.RSContainerMenus;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.refinedmods.refinedstorage.blockentity.ExternalStorageBlockEntity;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import net.minecraft.world.entity.player.Player;

public class ExternalStorageContainerMenu extends BaseContainerMenu {
    public ExternalStorageContainerMenu(ExternalStorageBlockEntity externalStorage, Player player, int windowId) {
        super(RSContainerMenus.EXTERNAL_STORAGE.get(), externalStorage, player, windowId);

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
