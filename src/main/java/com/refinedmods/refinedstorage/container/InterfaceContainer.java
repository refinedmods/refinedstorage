package com.refinedmods.refinedstorage.container;

import com.refinedmods.refinedstorage.RSContainers;
import com.refinedmods.refinedstorage.container.slot.OutputSlot;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.tile.InterfaceTile;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.SlotItemHandler;

public class InterfaceContainer extends BaseContainer {
    public InterfaceContainer(InterfaceTile tile, Player player, int windowId) {
        super(RSContainers.INTERFACE, tile, player, windowId);

        for (int i = 0; i < 9; ++i) {
            addSlot(new SlotItemHandler(tile.getNode().getImportItems(), i, 8 + (18 * i), 20));
        }

        for (int i = 0; i < 9; ++i) {
            addSlot(new FilterSlot(tile.getNode().getExportFilterItems(), i, 8 + (18 * i), 54, FilterSlot.FILTER_ALLOW_SIZE));
        }

        for (int i = 0; i < 9; ++i) {
            addSlot(new OutputSlot(tile.getNode().getExportItems(), i, 8 + (18 * i), 100));
        }

        for (int i = 0; i < 4; ++i) {
            addSlot(new SlotItemHandler(tile.getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        addPlayerInventory(8, 134);

        transferManager.addBiTransfer(player.getInventory(), tile.getNode().getUpgrades());
        transferManager.addBiTransfer(player.getInventory(), tile.getNode().getImportItems());
        transferManager.addTransfer(tile.getNode().getExportItems(), player.getInventory());
    }
}
