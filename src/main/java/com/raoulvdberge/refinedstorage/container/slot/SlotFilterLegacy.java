package com.raoulvdberge.refinedstorage.container.slot;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class SlotFilterLegacy extends Slot {
    private Supplier<Boolean> enableHandler = () -> true;

    public SlotFilterLegacy(IInventory inventory, int id, int x, int y) {
        super(inventory, id, x, y);
    }

    public SlotFilterLegacy(IInventory inventory, int id, int x, int y, Supplier<Boolean> enableHandler) {
        this(inventory, id, x, y);

        this.enableHandler = enableHandler;
    }

    @Override
    public boolean canTakeStack(EntityPlayer player) {
        return false;
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return true;
    }

    @Override
    public void putStack(@Nonnull ItemStack stack) {
        if (!stack.isEmpty()) {
            stack.setCount(1);
        }

        super.putStack(stack);
    }

    @Override
    public boolean isEnabled() {
        return enableHandler.get();
    }
}
