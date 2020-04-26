package com.raoulvdberge.refinedstorage.container;

import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilter;
import com.raoulvdberge.refinedstorage.container.slot.filter.SlotFilterFluid;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventory;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventoryFilter;
import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventoryFilterIcon;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerFilterIcon;
import com.raoulvdberge.refinedstorage.inventory.item.ItemHandlerFilterItems;
import com.raoulvdberge.refinedstorage.item.ItemFilter;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ContainerFilter extends ContainerBase {
    private ItemStack stack;

    public ContainerFilter(EntityPlayer player, ItemStack stack) {
        super(null, player);

        this.stack = stack;

        int y = 20;
        int x = 8;

        ItemHandlerFilterItems filter = new ItemHandlerFilterItems(stack);

        FluidInventoryFilterIcon fluidIcon = new FluidInventoryFilterIcon(stack);
        FluidInventory fluidFilter = new FluidInventoryFilter(stack);

        for (int i = 0; i < 27; ++i) {
            addSlotToContainer(new SlotFilter(filter, i, x, y).setEnableHandler(() -> ItemFilter.getType(stack) == IType.ITEMS));
            addSlotToContainer(new SlotFilterFluid(fluidFilter, i, x, y).setEnableHandler(() -> ItemFilter.getType(stack) == IType.FLUIDS));

            if ((i + 1) % 9 == 0) {
                x = 8;
                y += 18;
            } else {
                x += 18;
            }
        }

        addSlotToContainer(new SlotFilter(new ItemHandlerFilterIcon(stack), 0, 8, 117).setEnableHandler(() -> ItemFilter.getType(stack) == IType.ITEMS));
        addSlotToContainer(new SlotFilterFluid(fluidIcon, 0, 8, 117).setEnableHandler(() -> ItemFilter.getType(stack) == IType.FLUIDS));

        addPlayerInventory(8, 149);

        transferManager.addFilterTransfer(player.inventory, filter, fluidFilter, () -> ItemFilter.getType(stack));
    }

    public ItemStack getStack() {
        return stack;
    }

    @Override
    protected int getDisabledSlotNumber() {
        return getPlayer().inventory.currentItem;
    }
}
