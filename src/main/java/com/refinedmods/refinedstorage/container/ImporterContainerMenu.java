package com.refinedmods.refinedstorage.container;

import com.refinedmods.refinedstorage.RSContainerMenus;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.refinedmods.refinedstorage.blockentity.ImporterBlockEntity;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.SlotItemHandler;

public class ImporterContainerMenu extends BaseContainerMenu {
    public ImporterContainerMenu(ImporterBlockEntity importer, Player player, int windowId) {
        super(RSContainerMenus.IMPORTER.get(), importer, player, windowId);

        for (int i = 0; i < 4; ++i) {
            addSlot(new SlotItemHandler(importer.getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        for (int i = 0; i < 9; ++i) {
            addSlot(new FilterSlot(importer.getNode().getItemFilters(), i, 8 + (18 * i), 20).setEnableHandler(() -> importer.getNode().getType() == IType.ITEMS));
        }

        for (int i = 0; i < 9; ++i) {
            addSlot(new FluidFilterSlot(importer.getNode().getFluidFilters(), i, 8 + (18 * i), 20).setEnableHandler(() -> importer.getNode().getType() == IType.FLUIDS));
        }

        addPlayerInventory(8, 55);

        transferManager.addBiTransfer(player.getInventory(), importer.getNode().getUpgrades());
        transferManager.addFilterTransfer(player.getInventory(), importer.getNode().getItemFilters(), importer.getNode().getFluidFilters(), importer.getNode()::getType);
    }
}
