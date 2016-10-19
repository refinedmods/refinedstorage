package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.container.slot.SlotSpecimen;
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

        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new SlotSpecimen(filter, i, 8 + (i * 18), 20));
        }

        addPlayerInventory(8, 70);
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stack = null;

        Slot slot = getSlot(index);

        if (slot != null && slot.getHasStack()) {
            stack = slot.getStack();

            if (index > 9 - 1) {
                return mergeItemStackToSpecimen(stack, 0, 9);
            }

            return null;
        }

        return stack;
    }
}
