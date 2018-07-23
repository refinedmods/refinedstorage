package com.raoulvdberge.refinedstorage.container.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class SlotDisabled extends Slot {
    private Supplier<Boolean> enableHandler = () -> true;

    public SlotDisabled(IInventory inventory, int id, int x, int y) {
        super(inventory, id, x, y);
    }

    public SlotDisabled(IInventory inventory, int id, int x, int y, Supplier<Boolean> enableHandler) {
        this(inventory, id, x, y);

        this.enableHandler = enableHandler;
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return enableHandler.get();
    }
}
