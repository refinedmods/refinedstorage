package com.raoulvdberge.refinedstorage.inventory.item;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class ItemHandlerFilterIcon extends ItemStackHandler {
    private ItemStack stack;

    public ItemHandlerFilterIcon(ItemStack stack) {
        super(1);

        this.stack = stack;

        //TODO setStackInSlot(0, ItemFilter.getIcon(stack));
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);

        // TODO ItemFilter.setIcon(stack, getStackInSlot(0));
    }
}
