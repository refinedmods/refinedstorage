package com.raoulvdberge.refinedstorage.apiimpl.autocrafting.task.inserter;

import net.minecraft.item.ItemStack;

public class CraftingInserterItem {
    private ItemStack stack;
    private CraftingInserterItemStatus status;

    public CraftingInserterItem(ItemStack stack, CraftingInserterItemStatus status) {
        this.stack = stack;
        this.status = status;
    }

    public ItemStack getStack() {
        return stack;
    }

    public CraftingInserterItemStatus getStatus() {
        return status;
    }

    public void setStatus(CraftingInserterItemStatus status) {
        this.status = status;
    }
}
