package com.raoulvdberge.refinedstorage.container.slot.filter;

import com.raoulvdberge.refinedstorage.container.slot.BaseSlot;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventory;
import com.raoulvdberge.refinedstorage.util.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class FluidFilterSlot extends BaseSlot {
    public static final int FILTER_ALLOW_SIZE = 1;

    private int flags;
    private FluidInventory fluidInventory;

    public FluidFilterSlot(FluidInventory inventory, int inventoryIndex, int x, int y, int flags) {
        super(new ItemStackHandler(inventory.getSlots()), inventoryIndex, x, y);

        this.flags = flags;
        this.fluidInventory = inventory;
    }

    public FluidFilterSlot(FluidInventory inventory, int inventoryIndex, int x, int y) {
        this(inventory, inventoryIndex, x, y, 0);
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return false;
    }

    public void onContainerClicked(@Nonnull ItemStack stack) {
        fluidInventory.setFluid(getSlotIndex(), StackUtils.getFluid(stack, true).getValue());
    }

    public boolean isSizeAllowed() {
        return (flags & FILTER_ALLOW_SIZE) == FILTER_ALLOW_SIZE;
    }

    public FluidInventory getFluidInventory() {
        return fluidInventory;
    }
}
