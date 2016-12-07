package com.raoulvdberge.refinedstorage.inventory;

import com.raoulvdberge.refinedstorage.item.ItemGridFilter;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class ItemHandlerGridFilterIcon extends ItemStackHandler {
    private ItemStack stack;

    public ItemHandlerGridFilterIcon(ItemStack stack) {
        super(1);

        this.stack = stack;

        setStackInSlot(0, ItemGridFilter.getIcon(stack));
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);

        ItemGridFilter.setIcon(stack, getStackInSlot(0));
    }
}
