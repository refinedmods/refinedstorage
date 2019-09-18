package com.raoulvdberge.refinedstorage.container.slot.legacy;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;

import java.util.function.Supplier;

public class BaseLegacySlot extends Slot {
    private Supplier<Boolean> enableHandler = () -> true;

    public BaseLegacySlot(IInventory inventory, int inventoryIndex, int x, int y) {
        super(inventory, inventoryIndex, x, y);
    }

    public BaseLegacySlot setEnableHandler(Supplier<Boolean> enableHandler) {
        this.enableHandler = enableHandler;

        return this;
    }

    @Override
    public boolean isEnabled() {
        return enableHandler.get();
    }

}
