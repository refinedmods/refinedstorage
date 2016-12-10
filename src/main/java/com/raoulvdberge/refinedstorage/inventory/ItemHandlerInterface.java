package com.raoulvdberge.refinedstorage.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

public class ItemHandlerInterface implements IItemHandler {
    private IItemHandler importItems;
    private IItemHandler exportItems;

    public ItemHandlerInterface(IItemHandler importItems, IItemHandler exportItems) {
        this.importItems = importItems;
        this.exportItems = exportItems;
    }

    @Override
    public int getSlots() {
        return importItems.getSlots() + exportItems.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot < 9 ? importItems.getStackInSlot(slot) : exportItems.getStackInSlot(slot - 9);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        return slot < 9 ? importItems.insertItem(slot, stack, simulate) : stack;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return slot >= 9 ? exportItems.extractItem(slot - 9, amount, simulate) : null;
    }
}
