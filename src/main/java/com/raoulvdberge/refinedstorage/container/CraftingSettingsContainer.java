package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.container.slot.DisabledSlot;
import com.raoulvdberge.refinedstorage.container.slot.filter.DisabledFluidFilterSlot;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventory;
import com.raoulvdberge.refinedstorage.screen.grid.stack.GridStackFluid;
import com.raoulvdberge.refinedstorage.screen.grid.stack.GridStackItem;
import com.raoulvdberge.refinedstorage.screen.grid.stack.IGridStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class CraftingSettingsContainer extends BaseContainer {
    public CraftingSettingsContainer(PlayerEntity player, IGridStack stack) {
        super(null, null, player, 0);

        if (stack instanceof GridStackFluid) {
            FluidInventory inventory = new FluidInventory(1);

            inventory.setFluid(0, ((GridStackFluid) stack).getStack());

            addSlot(new DisabledFluidFilterSlot(inventory, 0, 89, 48));
        } else if (stack instanceof GridStackItem) {
            ItemStackHandler handler = new ItemStackHandler(1);

            handler.setStackInSlot(0, ItemHandlerHelper.copyStackWithSize(((GridStackItem) stack).getStack(), 1));

            addSlot(new DisabledSlot(handler, 0, 89, 48));
        }
    }
}
