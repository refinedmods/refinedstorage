package com.refinedmods.refinedstorage.container.slot.legacy;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

import java.util.function.BooleanSupplier;

public class LegacyBaseSlot extends Slot {
    private BooleanSupplier enableHandler = () -> true;

    public LegacyBaseSlot(Container inventory, int inventoryIndex, int x, int y) {
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
