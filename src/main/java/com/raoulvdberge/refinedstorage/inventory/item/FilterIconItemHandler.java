package com.raoulvdberge.refinedstorage.inventory.item;

import com.raoulvdberge.refinedstorage.item.FilterItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class FilterIconItemHandler extends ItemStackHandler {
    private ItemStack stack;

    public FilterIconItemHandler(ItemStack stack) {
        super(1);

        this.stack = stack;

        setStackInSlot(0, FilterItem.getIcon(stack));
    }

    @Override
    protected void onContentsChanged(int slot) {
        super.onContentsChanged(slot);

        FilterItem.setIcon(stack, getStackInSlot(0));
    }
}
