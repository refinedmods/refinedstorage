package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.RSContainers;
import com.raoulvdberge.refinedstorage.container.slot.SlotOutput;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilter;
import com.raoulvdberge.refinedstorage.tile.TileInterface;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerInterface extends ContainerBase {
    public ContainerInterface(TileInterface tile, PlayerEntity player, int windowId) {
        super(RSContainers.INTERFACE, tile, player, windowId);

        for (int i = 0; i < 9; ++i) {
            addSlot(new SlotItemHandler(tile.getNode().getImportItems(), i, 8 + (18 * i), 20));
        }

        for (int i = 0; i < 9; ++i) {
            addSlot(new SlotFilter(tile.getNode().getExportFilterItems(), i, 8 + (18 * i), 54, SlotFilter.FILTER_ALLOW_SIZE));
        }

        for (int i = 0; i < 9; ++i) {
            addSlot(new SlotOutput(tile.getNode().getExportItems(), i, 8 + (18 * i), 100));
        }

        for (int i = 0; i < 4; ++i) {
            addSlot(new SlotItemHandler(tile.getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        addPlayerInventory(8, 134);

        transferManager.addBiTransfer(player.inventory, tile.getNode().getUpgrades());
        transferManager.addBiTransfer(player.inventory, tile.getNode().getImportItems());
        transferManager.addTransfer(tile.getNode().getExportItems(), player.inventory);
    }
}
