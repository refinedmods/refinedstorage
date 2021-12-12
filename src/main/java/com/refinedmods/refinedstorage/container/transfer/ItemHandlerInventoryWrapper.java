package com.refinedmods.refinedstorage.container.transfer;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Objects;

class ItemHandlerInventoryWrapper implements IInventoryWrapper {
    private final IItemHandler handler;

    ItemHandlerInventoryWrapper(IItemHandler handler) {
        this.handler = handler;
    }

    @Override
    public InsertionResult insert(ItemStack stack) {
        return new InsertionResult(ItemHandlerHelper.insertItem(handler, stack, false));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ItemHandlerInventoryWrapper that = (ItemHandlerInventoryWrapper) o;

        return Objects.equals(handler, that.handler);
    }

    @Override
    public int hashCode() {
        return Objects.hash(handler);
    }
}
