package com.refinedmods.refinedstorage.container;

import com.refinedmods.refinedstorage.RSContainers;
import com.refinedmods.refinedstorage.container.slot.filter.FilterSlot;
import com.refinedmods.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.refinedmods.refinedstorage.tile.DiskManipulatorTile;
import com.refinedmods.refinedstorage.tile.config.IType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.SlotItemHandler;

public class DiskManipulatorContainer extends BaseContainer {
    public DiskManipulatorContainer(DiskManipulatorTile diskManipulator, PlayerEntity player, int windowId) {
        super(RSContainers.DISK_MANIPULATOR, diskManipulator, player, windowId);

        for (int i = 0; i < 4; ++i) {
            addSlot(new SlotItemHandler(diskManipulator.getNode().getUpgrades(), i, 187, 6 + (i * 18)));
        }

        for (int i = 0; i < 3; ++i) {
            addSlot(new SlotItemHandler(diskManipulator.getNode().getInputDisks(), i, 44, 57 + (i * 18)));
        }

        for (int i = 0; i < 3; ++i) {
            addSlot(new SlotItemHandler(diskManipulator.getNode().getOutputDisks(), i, 116, 57 + (i * 18)));
        }

        for (int i = 0; i < 9; ++i) {
            addSlot(new FilterSlot(diskManipulator.getNode().getItemFilters(), i, 8 + (18 * i), 20).setEnableHandler(() -> diskManipulator.getNode().getType() == IType.ITEMS));
        }

        for (int i = 0; i < 9; ++i) {
            addSlot(new FluidFilterSlot(diskManipulator.getNode().getFluidFilters(), i, 8 + (18 * i), 20).setEnableHandler(() -> diskManipulator.getNode().getType() == IType.FLUIDS));
        }

        addPlayerInventory(8, 129);

        transferManager.addBiTransfer(player.inventory, diskManipulator.getNode().getUpgrades());
        transferManager.addBiTransfer(player.inventory, diskManipulator.getNode().getInputDisks());
        transferManager.addTransfer(diskManipulator.getNode().getOutputDisks(), player.inventory);
        transferManager.addFilterTransfer(player.inventory, diskManipulator.getNode().getItemFilters(), diskManipulator.getNode().getFluidFilters(), diskManipulator.getNode()::getType);
    }
}
