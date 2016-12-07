package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.container.slot.SlotFilter;
import com.raoulvdberge.refinedstorage.inventory.ItemHandlerGridFilter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerGridFilter extends ContainerBase {
    private ItemHandlerGridFilter filter;
    private ItemStack stack;

    public ContainerGridFilter(EntityPlayer player, ItemStack stack) {
        super(null, player);

        this.stack = stack;
        this.filter = new ItemHandlerGridFilter(stack);

        int y = 20;
        int x = 8;

        for (int i = 0; i < 27; ++i) {
            addSlotToContainer(new SlotFilter(filter, i, x, y));

            if ((i + 1) % 9 == 0) {
                x = 8;
                y += 18;
            } else {
                x += 18;
            }
        }

        addPlayerInventory(8, 126);
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = ItemStack.EMPTY;

        Slot slot = getSlot(index);

        if (slot.getHasStack()) {
            stack = slot.getStack();

            if (index > 27 - 1) {
                return mergeItemStackToFilters(stack, 0, 27);
            }

            return ItemStack.EMPTY;
        }

        return stack;
    }
}
