package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.container.slot.SlotFilterType;
import com.raoulvdberge.refinedstorage.tile.TileDetector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerDetector extends ContainerBase {
    public ContainerDetector(TileDetector detector, EntityPlayer player) {
        super(detector, player);

        addSlotToContainer(new SlotFilterType(detector.getNode(), 0, 107, 20));

        addPlayerInventory(8, 55);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        Slot slot = getSlot(index);

        if (slot.getHasStack() && index > 0) {
            return mergeItemStackToFilters(slot.getStack(), 0, 1);
        }

        return ItemStack.EMPTY;
    }
}
