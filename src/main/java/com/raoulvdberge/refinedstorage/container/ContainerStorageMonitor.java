package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.container.slot.SlotFilterItemOrFluid;
import com.raoulvdberge.refinedstorage.tile.TileStorageMonitor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerStorageMonitor extends ContainerBase {
    public ContainerStorageMonitor(TileStorageMonitor storageMonitor, EntityPlayer player) {
        super(storageMonitor, player);

        addSlotToContainer(new SlotFilterItemOrFluid(storageMonitor.getNode(), 0, 80, 20));

        addPlayerInventory(8, 55);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        Slot slot = getSlot(index);

        ItemStack stack = slot.getStack();

        if (index > 0 && slot.getHasStack()) {
            return mergeItemStackToFilters(stack, 0, 1);
        }

        return stack;
    }
}
