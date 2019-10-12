package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.RSContainers;
import com.raoulvdberge.refinedstorage.container.slot.filter.FilterSlot;
import com.raoulvdberge.refinedstorage.container.slot.filter.FluidFilterSlot;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventory;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventoryFilter;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventoryFilterIcon;
import com.raoulvdberge.refinedstorage.inventory.item.FilterIconItemHandler;
import com.raoulvdberge.refinedstorage.inventory.item.FilterItemsItemHandler;
import com.raoulvdberge.refinedstorage.item.FilterItem;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class FilterContainer extends BaseContainer {
    private ItemStack stack;

    public FilterContainer(PlayerEntity player, ItemStack stack, int windowId) {
        super(RSContainers.FILTER, null, player, windowId);

        this.stack = stack;

        int y = 20;
        int x = 8;

        FilterItemsItemHandler filter = new FilterItemsItemHandler(stack);
        FluidInventory fluidFilter = new FluidInventoryFilter(stack);

        for (int i = 0; i < 27; ++i) {
            addSlot(new FilterSlot(filter, i, x, y).setEnableHandler(() -> FilterItem.getType(stack) == IType.ITEMS));
            addSlot(new FluidFilterSlot(fluidFilter, i, x, y).setEnableHandler(() -> FilterItem.getType(stack) == IType.FLUIDS));

            if ((i + 1) % 9 == 0) {
                x = 8;
                y += 18;
            } else {
                x += 18;
            }
        }

        addSlot(new FilterSlot(new FilterIconItemHandler(stack), 0, 8, 117).setEnableHandler(() -> FilterItem.getType(stack) == IType.ITEMS));
        addSlot(new FluidFilterSlot(new FluidInventoryFilterIcon(stack), 0, 8, 117).setEnableHandler(() -> FilterItem.getType(stack) == IType.FLUIDS));

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
