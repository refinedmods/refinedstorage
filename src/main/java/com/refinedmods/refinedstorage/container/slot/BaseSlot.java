package com.refinedmods.refinedstorage.container.slot;

import java.util.function.BooleanSupplier;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

public class BaseSlot extends SlotItemHandler {
    private BooleanSupplier enableHandler = () -> true;

    public BaseSlot(IItemHandler itemHandler, int inventoryIndex, int x, int y) {
        super(itemHandler, inventoryIndex, x, y);
    }

    public BaseSlot setEnableHandler(BooleanSupplier enableHandler) {
        this.enableHandler = enableHandler;

        return this;
    }

    @Override
    public boolean isActive() {
        return enableHandler.getAsBoolean();
    }
}
