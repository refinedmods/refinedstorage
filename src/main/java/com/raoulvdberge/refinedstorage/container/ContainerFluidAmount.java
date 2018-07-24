package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilterFluidDisabled;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fluids.FluidStack;

public class ContainerFluidAmount extends ContainerBase {
    public ContainerFluidAmount(EntityPlayer player, FluidStack stack) {
        super(null, player);

        FluidInventory inventory = new FluidInventory(1);

        inventory.setFluid(0, stack);

        addSlotToContainer(new SlotFilterFluidDisabled(inventory, 0, 89, 48, 0));
    }
}
