package com.refinedmods.refinedstorage.container.slot.filter;

import com.refinedmods.refinedstorage.container.slot.BaseSlot;
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import javax.annotation.Nonnull;

public class FluidFilterSlot extends BaseSlot {
    public static final int FILTER_ALLOW_SIZE = 1;
    public static final int FILTER_ALLOW_ALTERNATIVES = 2;

    private final int flags;
    private final FluidInventory fluidInventory;

    public FluidFilterSlot(FluidInventory inventory, int inventoryIndex, int x, int y, int flags) {
        super(new ItemStackHandler(inventory.getSlots()), inventoryIndex, x, y);

        this.flags = flags;
        this.fluidInventory = inventory;
    }

    public FluidFilterSlot(FluidInventory inventory, int inventoryIndex, int x, int y) {
        this(inventory, inventoryIndex, x, y, 0);
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        return false;
    }

    public void onContainerClicked(@Nonnull ItemStack stack) {
        fluidInventory.setFluid(getSlotIndex(), StackUtils.getFluid(stack, true).getValue());
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return false;
    }

    public boolean isSizeAllowed() {
        return (flags & FILTER_ALLOW_SIZE) == FILTER_ALLOW_SIZE;
    }

    public boolean isAlternativesAllowed() {
        return (flags & FILTER_ALLOW_ALTERNATIVES) == FILTER_ALLOW_ALTERNATIVES;
    }

    public FluidInventory getFluidInventory() {
        return fluidInventory;
    }

    // not overriding getHasStack as we do the tooltips ourselves
    public boolean hasStack() {
        return !fluidInventory.getFluid(getSlotIndex()).isEmpty();
    }
}
