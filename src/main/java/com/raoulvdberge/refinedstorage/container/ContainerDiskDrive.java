package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.RSContainers;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilter;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilterFluid;
import com.raoulvdberge.refinedstorage.tile.TileDiskDrive;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerDiskDrive extends ContainerBase {
    public ContainerDiskDrive(TileDiskDrive diskDrive, PlayerEntity player, int windowId) {
        super(RSContainers.DISK_DRIVE, diskDrive, player, windowId);

        int x = 80;
        int y = 54;

        for (int i = 0; i < 8; ++i) {
            addSlot(new SlotItemHandler(diskDrive.getNode().getDisks(), i, x + ((i % 2) * 18), y + Math.floorDiv(i, 2) * 18));
        }

        for (int i = 0; i < 9; ++i) {
            addSlot(new SlotFilter(diskDrive.getNode().getItemFilters(), i, 8 + (18 * i), 20).setEnableHandler(() -> diskDrive.getNode().getType() == IType.ITEMS));
        }

        for (int i = 0; i < 9; ++i) {
            addSlot(new SlotFilterFluid(diskDrive.getNode().getFluidFilters(), i, 8 + (18 * i), 20).setEnableHandler(() -> diskDrive.getNode().getType() == IType.FLUIDS));
        }

        addPlayerInventory(8, 141);

        transferManager.addBiTransfer(player.inventory, diskDrive.getNode().getDisks());
        transferManager.addFilterTransfer(player.inventory, diskDrive.getNode().getItemFilters(), diskDrive.getNode().getFluidFilters(), diskDrive.getNode()::getType);
    }
}
