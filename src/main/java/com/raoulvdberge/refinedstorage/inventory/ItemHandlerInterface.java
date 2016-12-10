package com.raoulvdberge.refinedstorage.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

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

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot < importItems.getSlots() ? importItems.getStackInSlot(slot) : exportItems.getStackInSlot(slot - importItems.getSlots());
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return slot < importItems.getSlots() ? importItems.insertItem(slot, stack, simulate) : stack;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return slot >= importItems.getSlots() ? exportItems.extractItem(slot - importItems.getSlots(), amount, simulate) : ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return slot < importItems.getSlots() ? importItems.getSlotLimit(slot) : exportItems.getSlotLimit(slot - importItems.getSlots());
    }
}
