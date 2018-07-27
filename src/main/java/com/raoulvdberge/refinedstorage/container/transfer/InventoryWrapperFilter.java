package com.raoulvdberge.refinedstorage.container.transfer;

import com.raoulvdberge.refinedstorage.inventory.fluid.FluidInventory;
import com.raoulvdberge.refinedstorage.tile.config.IType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.function.Supplier;

class InventoryWrapperFilter implements IInventoryWrapper {
    private InventoryWrapperFilterItem item;
    private InventoryWrapperFilterFluid fluid;
    private Supplier<Integer> typeGetter;

    InventoryWrapperFilter(IItemHandlerModifiable itemTo, FluidInventory fluidTo, Supplier<Integer> typeGetter) {
        this.item = new InventoryWrapperFilterItem(itemTo);
        this.fluid = new InventoryWrapperFilterFluid(fluidTo);
        this.typeGetter = typeGetter;
    }

    @Override
    public InsertionResult insert(ItemStack stack) {
        return typeGetter.get() == IType.ITEMS ? item.insert(stack) : fluid.insert(stack);
    }
}
