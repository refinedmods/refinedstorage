package com.refinedmods.refinedstorage.container;

import com.refinedmods.refinedstorage.RSContainerMenus;
import com.refinedmods.refinedstorage.container.slot.OutputSlot;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.blockentity.InterfaceBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.SlotItemHandler;

public class InterfaceContainerMenu extends BaseContainerMenu {
    public InterfaceContainerMenu(InterfaceBlockEntity blockEntity, Player player, int windowId) {
        super(RSContainerMenus.INTERFACE, blockEntity, player, windowId);

        for (int i = 0; i < 9; ++i) {
            addSlot(new SlotItemHandler(blockEntity.getNode().getImportItems(), i, 8 + (18 * i), 20));
        }

        for (int i = 0; i < 9; ++i) {
            addSlot(new FilterSlot(blockEntity.getNode().getExportFilterItems(), i, 8 + (18 * i), 54, FilterSlot.FILTER_ALLOW_SIZE));
        }

        for (int i = 0; i < 9; ++i) {
            addSlot(new OutputSlot(blockEntity.getNode().getExportItems(), i, 8 + (18 * i), 100));
        }

        for (int i = 0; i < 4; ++i) {
            addSlot(new SlotItemHandler(blockEntity.getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        addPlayerInventory(8, 134);

        transferManager.addBiTransfer(player.getInventory(), blockEntity.getNode().getUpgrades());
        transferManager.addBiTransfer(player.getInventory(), blockEntity.getNode().getImportItems());
        transferManager.addTransfer(blockEntity.getNode().getExportItems(), player.getInventory());
    }
}
