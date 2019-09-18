package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.container.slot.filter.DisabledFluidFilterSlot;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fluids.FluidStack;

public class FluidAmountContainer extends BaseContainer {
    public FluidAmountContainer(PlayerEntity player, FluidStack stack) {
        super(null, null, player, 0);

        FluidInventory inventory = new FluidInventory(1);

        inventory.setFluid(0, stack);

        addSlot(new DisabledFluidFilterSlot(inventory, 0, 89, 48, 0));
    }
}
