package com.refinedmods.refinedstorage.container.slot.legacy;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;

import java.util.function.BooleanSupplier;

public class LegacyBaseSlot extends Slot {
    private BooleanSupplier enableHandler = () -> true;

    public LegacyBaseSlot(IInventory inventory, int inventoryIndex, int x, int y) {
        super(inventory, inventoryIndex, x, y);
    }

    public LegacyBaseSlot setEnableHandler(BooleanSupplier enableHandler) {
        this.enableHandler = enableHandler;

        return this;
    }

    @Override
    public boolean isActive() {
        return enableHandler.getAsBoolean();
    }
}
