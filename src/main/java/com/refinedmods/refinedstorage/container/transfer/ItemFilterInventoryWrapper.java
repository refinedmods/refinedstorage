package com.refinedmods.refinedstorage.container.transfer;

import com.refinedmods.refinedstorage.apiimpl.API;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemHandlerHelper;

class ItemFilterInventoryWrapper implements IInventoryWrapper {
    private final IItemHandlerModifiable filterInv;

    ItemFilterInventoryWrapper(IItemHandlerModifiable filterInv) {
        this.filterInv = filterInv;
    }

    @Override
    public InsertionResult insert(ItemStack stack) {
        InsertionResult stop = new InsertionResult(InsertionResultType.STOP);

        for (int i = 0; i < filterInv.getSlots(); ++i) {
            if (API.instance().getComparer().isEqualNoQuantity(filterInv.getStackInSlot(i), stack)) {
                return stop;
            }
        }

        for (int i = 0; i < filterInv.getSlots(); ++i) {
            if (filterInv.getStackInSlot(i).isEmpty()) {
                filterInv.setStackInSlot(i, ItemHandlerHelper.copyStackWithSize(stack, 1));

                break;
            }
        }

        return stop;
    }
}
