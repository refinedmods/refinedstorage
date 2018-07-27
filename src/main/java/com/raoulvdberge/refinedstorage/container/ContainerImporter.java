package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilter;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilterFluid;
import com.raoulvdberge.refinedstorage.tile.TileImporter;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerImporter extends ContainerBase {
    public ContainerImporter(TileImporter importer, EntityPlayer player) {
        super(importer, player);

        for (int i = 0; i < 4; ++i) {
            addSlotToContainer(new SlotItemHandler(importer.getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotFilter(importer.getNode().getItemFilters(), i, 8 + (18 * i), 20).setEnableHandler(() -> importer.getNode().getType() == IType.ITEMS));
        }

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotFilterFluid(importer.getNode().getFluidFilters(), i, 8 + (18 * i), 20).setEnableHandler(() -> importer.getNode().getType() == IType.FLUIDS));
        }

        addPlayerInventory(8, 55);

        transferManager.addBiTransfer(player.inventory, importer.getNode().getUpgrades());
        transferManager.addFilterTransfer(player.inventory, importer.getNode().getItemFilters(), importer.getNode().getFluidFilters(), importer.getNode()::getType);
    }
}
