package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.RSContainers;
import com.raoulvdberge.refinedstorage.container.slot.filter.FilterSlot;
import com.raoulvdberge.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.raoulvdberge.refinedstorage.item.UpgradeItem;
import com.raoulvdberge.refinedstorage.tile.ExporterTile;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.SlotItemHandler;

public class ExporterContainer extends BaseContainer {
    private final ExporterTile exporter;

    public ExporterContainer(ExporterTile exporter, PlayerEntity player, int windowId) {
        super(RSContainers.EXPORTER, exporter, player, windowId);

        this.exporter = exporter;

        initSlots();
    }

    public void initSlots() {
        this.inventorySlots.clear();
        this.inventoryItemStacks.clear();

        this.transferManager.clearTransfers();

        for (int i = 0; i < 4; ++i) {
            addSlot(new SlotItemHandler(exporter.getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        for (int i = 0; i < 9; ++i) {
            addSlot(new FilterSlot(
                exporter.getNode().getItemFilters(),
                i,
                8 + (18 * i),
                20,
                exporter.getNode().getUpgrades().hasUpgrade(UpgradeItem.Type.REGULATOR) ? FilterSlot.FILTER_ALLOW_SIZE : 0
            ).setEnableHandler(() -> exporter.getNode().getType() == IType.ITEMS));
        }

        for (int i = 0; i < 9; ++i) {
            addSlot(new FluidFilterSlot(
                exporter.getNode().getFluidFilters(),
                i,
                8 + (18 * i),
                20,
                exporter.getNode().getUpgrades().hasUpgrade(UpgradeItem.Type.REGULATOR) ? FluidFilterSlot.FILTER_ALLOW_SIZE : 0
            ).setEnableHandler(() -> exporter.getNode().getType() == IType.FLUIDS));
        }

        addPlayerInventory(8, 55);

        transferManager.addBiTransfer(getPlayer().inventory, exporter.getNode().getUpgrades());
        transferManager.addFilterTransfer(getPlayer().inventory, exporter.getNode().getItemFilters(), exporter.getNode().getFluidFilters(), exporter.getNode()::getType);
    }
}
