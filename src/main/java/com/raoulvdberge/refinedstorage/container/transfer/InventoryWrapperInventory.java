package com.raoulvdberge.refinedstorage.container.transfer;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.RangedWrapper;

import java.util.Objects;

class InventoryWrapperInventory implements IInventoryWrapper {
    private IInventory inventory;
    private IItemHandler wrapper;

    InventoryWrapperInventory(IInventory inventory) {
        this.inventory = inventory;

        if (inventory instanceof InventoryPlayer) {
            // Don't use PlayerMainInvWrapper to avoid stack animations.
            this.wrapper = new RangedWrapper(new InvWrapper(inventory), 0, ((InventoryPlayer) inventory).mainInventory.size());
        } else {
            this.wrapper = new InvWrapper(inventory);
        }
    }

    @Override
    public InsertionResult insert(ItemStack stack) {
        return new InsertionResult(ItemHandlerHelper.insertItem(wrapper, stack, false));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        InventoryWrapperInventory that = (InventoryWrapperInventory) o;

        return Objects.equals(inventory, that.inventory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inventory);
    }
}
