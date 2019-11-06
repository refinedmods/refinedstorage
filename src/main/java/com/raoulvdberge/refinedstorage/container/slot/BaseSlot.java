package com.raoulvdberge.refinedstorage.container.slot;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.function.Supplier;

public class BaseSlot extends SlotItemHandler {
    private Supplier<Boolean> enableHandler = () -> true;

    public BaseSlot(IItemHandler itemHandler, int inventoryIndex, int x, int y) {
        super(itemHandler, inventoryIndex, x, y);
    }

    public BaseSlot setEnableHandler(Supplier<Boolean> enableHandler) {
        this.enableHandler = enableHandler;

        return this;
    }

    @Override
    public boolean isEnabled() {
        return enableHandler.get();
    }
}
