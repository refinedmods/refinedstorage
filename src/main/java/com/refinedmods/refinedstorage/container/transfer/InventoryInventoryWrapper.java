package com.refinedmods.refinedstorage.container.transfer;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.RangedWrapper;

import java.util.Objects;

class InventoryInventoryWrapper implements IInventoryWrapper {
    private final Container inventory;
    private final IItemHandler wrapper;

    InventoryInventoryWrapper(Container inventory) {
        this.inventory = inventory;

        if (inventory instanceof Inventory) {
            // Don't use PlayerMainInvWrapper to avoid stack animations.
            this.wrapper = new RangedWrapper(new InvWrapper(inventory), 0, ((Inventory) inventory).items.size());
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

        InventoryInventoryWrapper that = (InventoryInventoryWrapper) o;

        return Objects.equals(inventory, that.inventory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inventory);
    }
}
