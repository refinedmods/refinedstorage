package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.RSContainers;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilter;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilterFluid;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventory;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventoryFilter;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventoryFilterIcon;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerFilterIcon;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerFilterItems;
import com.raoulvdberge.refinedstorage.item.FilterItem;
import com.raoulvdberge.refinedstorage.tile.config.IType;
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
        FluidInventory fluidFilter = new FluidInventoryFilter(stack);

        for (int i = 0; i < 27; ++i) {
            addSlot(new SlotFilter(filter, i, x, y).setEnableHandler(() -> FilterItem.getType(stack) == IType.ITEMS));
            addSlot(new SlotFilterFluid(fluidFilter, i, x, y).setEnableHandler(() -> FilterItem.getType(stack) == IType.FLUIDS));

            if ((i + 1) % 9 == 0) {
                x = 8;
                y += 18;
            } else {
                x += 18;
            }
        }

        addSlot(new SlotFilter(new ItemHandlerFilterIcon(stack), 0, 8, 117).setEnableHandler(() -> FilterItem.getType(stack) == IType.ITEMS));
        addSlot(new SlotFilterFluid(new FluidInventoryFilterIcon(stack), 0, 8, 117).setEnableHandler(() -> FilterItem.getType(stack) == IType.FLUIDS));

        addPlayerInventory(8, 149);

        transferManager.addFilterTransfer(player.inventory, filter, fluidFilter, () -> FilterItem.getType(stack));
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    protected boolean isHeldItemDisabled() {
        return true;
    }
}
