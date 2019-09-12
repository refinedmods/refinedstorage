package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.RSContainers;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventory;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventoryFilter;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventoryFilterIcon;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerFilterItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class ContainerFilter extends ContainerBase {
    private ItemStack stack;

    public ContainerFilter(PlayerEntity player, ItemStack stack, int windowId) {
        super(RSContainers.FILTER, null, player, windowId);

        this.stack = stack;

        int y = 20;
        int x = 8;

        ItemHandlerFilterItems filter = new ItemHandlerFilterItems(stack);

        FluidInventoryFilterIcon fluidIcon = new FluidInventoryFilterIcon(stack);
        FluidInventory fluidFilter = new FluidInventoryFilter(stack);

        for (int i = 0; i < 27; ++i) {
            // TODO addSlot(new SlotFilter(filter, i, x, y).setEnableHandler(() -> ItemFilter.getType(stack) == IType.ITEMS));
            // TODO addSlot(new SlotFilterFluid(fluidFilter, i, x, y).setEnableHandler(() -> ItemFilter.getType(stack) == IType.FLUIDS));

            if ((i + 1) % 9 == 0) {
                x = 8;
                y += 18;
            } else {
                x += 18;
            }
        }

        // TODO addSlot(new SlotFilter(new ItemHandlerFilterIcon(stack), 0, 8, 117).setEnableHandler(() -> ItemFilter.getType(stack) == IType.ITEMS));
        // TODO addSlot(new SlotFilterFluid(fluidIcon, 0, 8, 117).setEnableHandler(() -> ItemFilter.getType(stack) == IType.FLUIDS));

        addPlayerInventory(8, 149);

        // TODO transferManager.addFilterTransfer(player.inventory, filter, fluidFilter, () -> ItemFilter.getType(stack));
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    protected boolean isHeldItemDisabled() {
        return true;
    }
}
