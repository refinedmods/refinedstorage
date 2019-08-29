package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.container.slot.SlotOutput;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilter;
import com.raoulvdberge.refinedstorage.tile.TileInterface;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerInterface extends ContainerBase {
    public ContainerInterface(TileInterface tile, PlayerEntity player) {
        super(tile, player);

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotItemHandler(tile.getNode().getImportItems(), i, 8 + (18 * i), 20));
        }

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotFilter(tile.getNode().getExportFilterItems(), i, 8 + (18 * i), 54, SlotFilter.FILTER_ALLOW_SIZE));
        }

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotOutput(tile.getNode().getExportItems(), i, 8 + (18 * i), 100));
        }

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotItemHandler(tile.getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        addPlayerInventory(8, 134);

        transferManager.addBiTransfer(player.inventory, tile.getNode().getUpgrades());
        transferManager.addBiTransfer(player.inventory, tile.getNode().getImportItems());
        transferManager.addTransfer(tile.getNode().getExportItems(), player.inventory);
    }
}
