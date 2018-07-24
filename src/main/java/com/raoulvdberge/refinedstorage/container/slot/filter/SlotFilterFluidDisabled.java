package com.raoulvdberge.refinedstorage.container.slot.filter;

import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventory;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class SlotFilterFluidDisabled extends SlotFilterFluid {
    public SlotFilterFluidDisabled(FluidInventory inventory, int inventoryIndex, int x, int y, int flags) {
        super(inventory, inventoryIndex, x, y, flags);
    }

    public SlotFilterFluidDisabled(FluidInventory inventory, int inventoryIndex, int x, int y) {
        super(inventory, inventoryIndex, x, y);
    }

    @Override
    public void onContainerClicked(@Nonnull ItemStack stack) {
        // NO OP
    }
}
