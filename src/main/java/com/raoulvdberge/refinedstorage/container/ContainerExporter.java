package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilter;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilterFluid;
import com.raoulvdberge.refinedstorage.tile.TileExporter;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerExporter extends ContainerBase {
    public ContainerExporter(TileExporter exporter, EntityPlayer player) {
        super(exporter, player);

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotItemHandler(exporter.getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotFilter(exporter.getNode().getItemFilters(), i, 8 + (18 * i), 20).setEnableHandler(() -> exporter.getNode().getType() == IType.ITEMS));
        }

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotFilterFluid(exporter.getNode().getFluidFilters(), i, 8 + (18 * i), 20).setEnableHandler(() -> exporter.getNode().getType() == IType.FLUIDS));
        }

        addPlayerInventory(8, 55);

        transferManager.addBiTransfer(player.inventory, exporter.getNode().getUpgrades());
        transferManager.addFilterTransfer(player.inventory, exporter.getNode().getItemFilters(), exporter.getNode().getFluidFilters(), exporter.getNode()::getType);
    }
}
