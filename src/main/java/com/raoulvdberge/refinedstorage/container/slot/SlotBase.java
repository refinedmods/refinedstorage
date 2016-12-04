package com.raoulvdberge.refinedstorage.container.slot;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

// @todo: Forge issue #3497
public class SlotBase extends SlotItemHandler {
    public SlotBase(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    @Override
    public int getSlotStackLimit() {
        return getItemHandler().getSlotLimit(getSlotIndex());
    }
}
