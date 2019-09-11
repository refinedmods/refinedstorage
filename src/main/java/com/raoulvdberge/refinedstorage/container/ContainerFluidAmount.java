package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilterFluidDisabled;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fluids.FluidStack;

public class ContainerFluidAmount extends ContainerBase {
    public ContainerFluidAmount(PlayerEntity player, FluidStack stack) {
        super(null, null, player, 0);

        FluidInventory inventory = new FluidInventory(1);

        inventory.setFluid(0, stack);

        addSlot(new SlotFilterFluidDisabled(inventory, 0, 89, 48, 0));
    }
}
