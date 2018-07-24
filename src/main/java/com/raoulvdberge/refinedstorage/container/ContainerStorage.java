package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilter;
import com.raoulvdberge.refinedstorage.tile.TileStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerStorage extends ContainerBase {
    public ContainerStorage(TileStorage storage, EntityPlayer player) {
        super(storage, player);

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotFilter(storage.getNode().getFilters(), i, 8 + (18 * i), 20));
        }

        addPlayerInventory(8, 141);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        Slot slot = getSlot(index);

        if (slot.getHasStack() && index > 8) {
            return transferToFilters(slot.getStack(), 0, 9);
        }

        return ItemStack.EMPTY;
    }
}
