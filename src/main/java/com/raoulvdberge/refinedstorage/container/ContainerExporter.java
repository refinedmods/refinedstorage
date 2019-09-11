package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.RSContainers;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilter;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilterFluid;
import com.raoulvdberge.refinedstorage.tile.TileExporter;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerExporter extends ContainerBase {
    public ContainerExporter(TileExporter exporter, PlayerEntity player, int windowId) {
        super(RSContainers.EXPORTER, exporter, player, windowId);

        for (int i = 0; i < 4; ++i) {
            addSlot(new SlotItemHandler(exporter.getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        for (int i = 0; i < 9; ++i) {
            addSlot(new SlotFilter(exporter.getNode().getItemFilters(), i, 8 + (18 * i), 20).setEnableHandler(() -> exporter.getNode().getType() == IType.ITEMS));
        }

        for (int i = 0; i < 9; ++i) {
            addSlot(new SlotFilterFluid(exporter.getNode().getFluidFilters(), i, 8 + (18 * i), 20).setEnableHandler(() -> exporter.getNode().getType() == IType.FLUIDS));
        }

        addPlayerInventory(8, 55);

        transferManager.addBiTransfer(player.inventory, exporter.getNode().getUpgrades());
        transferManager.addFilterTransfer(player.inventory, exporter.getNode().getItemFilters(), exporter.getNode().getFluidFilters(), exporter.getNode()::getType);
    }
}
